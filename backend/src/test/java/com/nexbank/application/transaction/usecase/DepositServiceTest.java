package com.nexbank.application.transaction.usecase;

import com.nexbank.application.account.port.out.AccountRepository;
import com.nexbank.application.shared.port.out.MetricsPort;
import com.nexbank.application.shared.port.out.OutboxEventRepository;
import com.nexbank.application.transaction.port.in.DepositCommand;
import com.nexbank.application.transaction.port.out.TransactionRepository;
import com.nexbank.domain.account.Account;
import com.nexbank.domain.account.AccountNumber;
import com.nexbank.domain.account.AccountStatus;
import com.nexbank.domain.shared.Currency;
import com.nexbank.domain.shared.Money;
import com.nexbank.domain.shared.exception.BusinessRuleException;
import com.nexbank.domain.transaction.FinancialTransaction;
import com.nexbank.domain.transaction.TransactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepositServiceTest {

    @Mock private AccountRepository accountRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private OutboxEventRepository outboxEventRepository;
    @Mock private MetricsPort metricsPort;
    @InjectMocks private DepositService depositService;

    private UUID customerId;
    private UUID accountId;
    private Account account;

    @BeforeEach void setUp() {
        customerId = UUID.randomUUID();
        accountId = UUID.randomUUID();
        account = Account.builder()
            .id(accountId).accountNumber(AccountNumber.of("1234567890"))
            .customerId(customerId).currency(Currency.BRL)
            .balance(Money.of(BigDecimal.ZERO, Currency.BRL))
            .status(AccountStatus.ACTIVE)
            .openedAt(Instant.now()).updatedAt(Instant.now()).version(0L).build();
    }

    @Test void shouldDepositSuccessfully() {
        Money amount = Money.of(new BigDecimal("100.00"), Currency.BRL);
        DepositCommand cmd = new DepositCommand(customerId, accountId, amount, "key-001");

        when(transactionRepository.findByIdempotencyKey("key-001")).thenReturn(Optional.empty());
        when(accountRepository.findByIdWithLock(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FinancialTransaction result = depositService.deposit(cmd);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(result.getAmount()).isEqualTo(amount);
        verify(outboxEventRepository).save(any(), any(), eq("TransactionCompleted"), any());
    }

    @Test void shouldReturnExistingTransactionOnDuplicateKey() {
        FinancialTransaction existing = FinancialTransaction.builder()
            .id(UUID.randomUUID()).type(com.nexbank.domain.transaction.TransactionType.DEPOSIT)
            .destinationAccountId(accountId).amount(Money.of(BigDecimal.TEN, Currency.BRL))
            .status(TransactionStatus.COMPLETED).idempotencyKey("key-001").createdAt(Instant.now()).build();

        when(transactionRepository.findByIdempotencyKey("key-001")).thenReturn(Optional.of(existing));
        DepositCommand cmd = new DepositCommand(customerId, accountId, Money.of(BigDecimal.TEN, Currency.BRL), "key-001");

        FinancialTransaction result = depositService.deposit(cmd);
        assertThat(result).isSameAs(existing);
        verify(accountRepository, never()).findByIdWithLock(any());
    }

    @Test void shouldRejectDepositToAnotherCustomerAccount() {
        UUID otherId = UUID.randomUUID();
        DepositCommand cmd = new DepositCommand(otherId, accountId, Money.of(BigDecimal.TEN, Currency.BRL), "key-002");

        when(transactionRepository.findByIdempotencyKey(any())).thenReturn(Optional.empty());
        when(accountRepository.findByIdWithLock(accountId)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> depositService.deposit(cmd))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessageContaining("Access denied");
    }
}
