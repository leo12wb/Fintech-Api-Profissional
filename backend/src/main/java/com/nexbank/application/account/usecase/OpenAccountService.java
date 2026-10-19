package com.nexbank.application.account.usecase;

import com.nexbank.application.account.port.in.OpenAccountCommand;
import com.nexbank.application.account.port.in.OpenAccountUseCase;
import com.nexbank.application.account.port.out.AccountRepository;
import com.nexbank.application.customer.port.out.CustomerRepository;
import com.nexbank.application.shared.port.out.OutboxEventRepository;
import com.nexbank.domain.account.Account;
import com.nexbank.domain.account.AccountNumber;
import com.nexbank.domain.account.AccountStatus;
import com.nexbank.domain.shared.Money;
import com.nexbank.domain.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAccountService implements OpenAccountUseCase {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final OutboxEventRepository outboxEventRepository;

    @Override
    @Transactional
    public Account openAccount(OpenAccountCommand command) {
        customerRepository.findById(command.customerId())
            .filter(c -> c.isActive())
            .orElseThrow(() -> new BusinessRuleException("CUSTOMER_NOT_FOUND_OR_INACTIVE",
                "Customer not found or not active: " + command.customerId()));

        AccountNumber number = generateUniqueAccountNumber();

        Account account = Account.builder()
            .id(UUID.randomUUID())
            .accountNumber(number)
            .customerId(command.customerId())
            .currency(command.currency())
            .balance(Money.of(BigDecimal.ZERO, command.currency()))
            .status(AccountStatus.ACTIVE)
            .openedAt(Instant.now())
            .updatedAt(Instant.now())
            .version(0L)
            .build();

        Account saved = accountRepository.save(account);
        outboxEventRepository.save("Account", saved.getId().toString(), "AccountCreated",
            "{\"accountId\":\"" + saved.getId() + "\",\"customerId\":\"" + saved.getCustomerId() + "\"}");

        log.info("Account opened: {} for customer: {}", saved.getAccountNumber().getValue(), saved.getCustomerId());
        return saved;
    }

    private AccountNumber generateUniqueAccountNumber() {
        for (int i = 0; i < 10; i++) {
            AccountNumber candidate = AccountNumber.generate();
            if (!accountRepository.existsByAccountNumber(candidate.getValue())) {
                return candidate;
            }
        }
        throw new IllegalStateException("Could not generate unique account number after 10 attempts");
    }
}
