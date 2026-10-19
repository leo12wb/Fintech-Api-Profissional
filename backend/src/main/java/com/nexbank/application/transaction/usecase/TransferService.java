package com.nexbank.application.transaction.usecase;

import com.nexbank.application.account.port.out.AccountRepository;
import com.nexbank.application.shared.port.out.MetricsPort;
import com.nexbank.application.shared.port.out.OutboxEventRepository;
import com.nexbank.application.transaction.port.in.TransferCommand;
import com.nexbank.application.transaction.port.in.TransferUseCase;
import com.nexbank.application.transaction.port.out.TransactionRepository;
import com.nexbank.domain.account.Account;
import com.nexbank.domain.shared.exception.BusinessRuleException;
import com.nexbank.domain.transaction.FinancialTransaction;
import com.nexbank.domain.transaction.TransactionStatus;
import com.nexbank.domain.transaction.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService implements TransferUseCase {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final MetricsPort metricsPort;

    @Override
    @Transactional
    public FinancialTransaction transfer(TransferCommand command) {
        // Idempotency check
        Optional<FinancialTransaction> existing = transactionRepository.findByIdempotencyKey(command.idempotencyKey());
        if (existing.isPresent()) {
            log.info("Returning existing transfer for idempotency key: {}", command.idempotencyKey());
            return existing.get();
        }

        // Acquire locks in consistent order to prevent deadlocks
        UUID firstId = command.sourceAccountId().compareTo(command.destinationAccountId()) < 0
            ? command.sourceAccountId() : command.destinationAccountId();
        UUID secondId = firstId.equals(command.sourceAccountId())
            ? command.destinationAccountId() : command.sourceAccountId();

        Account first = accountRepository.findByIdWithLock(firstId)
            .orElseThrow(() -> new BusinessRuleException("ACCOUNT_NOT_FOUND", "Account not found: " + firstId));
        Account second = accountRepository.findByIdWithLock(secondId)
            .orElseThrow(() -> new BusinessRuleException("ACCOUNT_NOT_FOUND", "Account not found: " + secondId));

        Account source = first.getId().equals(command.sourceAccountId()) ? first : second;
        Account destination = first.getId().equals(command.destinationAccountId()) ? first : second;

        if (!source.belongsTo(command.customerId())) {
            throw new BusinessRuleException("ACCESS_DENIED", "Access denied to source account: " + command.sourceAccountId());
        }

        source.assertActive();
        destination.assertActive();

        Account debited = source.debit(command.amount());
        Account credited = destination.credit(command.amount());

        accountRepository.save(debited);
        accountRepository.save(credited);

        FinancialTransaction tx = FinancialTransaction.builder()
            .id(UUID.randomUUID())
            .type(TransactionType.TRANSFER)
            .sourceAccountId(command.sourceAccountId())
            .destinationAccountId(command.destinationAccountId())
            .amount(command.amount())
            .status(TransactionStatus.COMPLETED)
            .idempotencyKey(command.idempotencyKey())
            .createdAt(Instant.now())
            .build();

        FinancialTransaction saved = transactionRepository.save(tx);

        outboxEventRepository.save("Transaction", saved.getId().toString(), "TransactionCompleted",
            "{\"transactionId\":\"" + saved.getId() + "\",\"type\":\"TRANSFER\",\"sourceAccountId\":\"" + command.sourceAccountId() + "\",\"destinationAccountId\":\"" + command.destinationAccountId() + "\"}");

        metricsPort.recordTransfer(command.amount());
        log.info("Transfer completed: {} from {} to {}", command.amount(), command.sourceAccountId(), command.destinationAccountId());
        return saved;
    }
}
