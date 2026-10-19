package com.nexbank.application.transaction.usecase;

import com.nexbank.application.account.port.out.AccountRepository;
import com.nexbank.application.shared.port.out.MetricsPort;
import com.nexbank.application.shared.port.out.OutboxEventRepository;
import com.nexbank.application.transaction.port.in.DepositCommand;
import com.nexbank.application.transaction.port.in.DepositUseCase;
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
public class DepositService implements DepositUseCase {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final MetricsPort metricsPort;

    @Override
    @Transactional
    public FinancialTransaction deposit(DepositCommand command) {
        // Idempotency check
        Optional<FinancialTransaction> existing = transactionRepository.findByIdempotencyKey(command.idempotencyKey());
        if (existing.isPresent()) {
            log.info("Returning existing deposit for idempotency key: {}", command.idempotencyKey());
            return existing.get();
        }

        Account account = accountRepository.findByIdWithLock(command.accountId())
            .orElseThrow(() -> new BusinessRuleException("ACCOUNT_NOT_FOUND", "Account not found: " + command.accountId()));

        if (!account.belongsTo(command.customerId())) {
            throw new BusinessRuleException("ACCESS_DENIED", "Access denied to account: " + command.accountId());
        }

        account.assertActive();

        Account credited = account.credit(command.amount());
        accountRepository.save(credited);

        FinancialTransaction tx = FinancialTransaction.builder()
            .id(UUID.randomUUID())
            .type(TransactionType.DEPOSIT)
            .destinationAccountId(command.accountId())
            .amount(command.amount())
            .status(TransactionStatus.COMPLETED)
            .idempotencyKey(command.idempotencyKey())
            .createdAt(Instant.now())
            .build();

        FinancialTransaction saved = transactionRepository.save(tx);

        outboxEventRepository.save("Transaction", saved.getId().toString(), "TransactionCompleted",
            "{\"transactionId\":\"" + saved.getId() + "\",\"type\":\"DEPOSIT\",\"accountId\":\"" + command.accountId() + "\"}");

        metricsPort.recordDeposit(command.amount());
        log.info("Deposit completed: {} to account {}", command.amount(), command.accountId());
        return saved;
    }
}
