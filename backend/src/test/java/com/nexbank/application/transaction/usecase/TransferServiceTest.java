package com.nexbank.application.transaction.usecase;

import com.nexbank.application.account.port.out.AccountRepository;
import com.nexbank.application.shared.port.out.MetricsPort;
import com.nexbank.application.shared.port.out.OutboxEventRepository;
import com.nexbank.application.transaction.port.in.TransferCommand;
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
class TransferServiceTest {

    @Mock private AccountRepository accountRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private OutboxEventRepository outboxEventRepository;
    @Mock private MetricsPort metricsPort;
    @InjectMocks private TransferService transferService;

    private UUID customerId;
    private UUID sourceId;
    private UUID destId;
    private Account source;
    private Account dest;

    @BeforeEach void setUp() {
        customerId = UUID.randomUUID();
        // Ensure consistent UUID ordering for lock acquisition in test
        sourceId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        destId = UUID.fromString("00000000-0000-0000-0000-000000000002");

        source = Account.builder().id(sourceId).accountNumber(AccountNumber.of("1111111111"))
            .customerId(customerId).currency(Currency.BRL)
            .balance(Money.of(new BigDecimal("500.00"), Currency.BRL))
            .status(AccountStatus.ACTIVE).openedAt(Instant.now()).updatedAt(Instant.now()).version(0L).build();

        dest = Account.builder().id(destId).accountNumber(AccountNumber.of("2222222222"))
            .customerId(UUID.randomUUID()).currency(Currency.BRL)
            .balance(Money.of(BigDecimal.ZERO, Currency.BRL))
            .status(AccountStatus.ACTIVE).openedAt(Instant.now()).updatedAt(Instant.now()).version(0L).build();
    }

    @Test void shouldTransferSuccessfully() {
        Money amount = Money.of(new BigDecimal("100.00"), Currency.BRL);
        TransferCommand cmd = new TransferCommand(customerId, sourceId, destId, amount, "key-t001");

        when(transactionRepository.findByIdempotencyKey(any())).thenReturn(Optional.empty());
        when(accountRepository.findByIdWithLock(sourceId)).thenReturn(Optional.of(source));
        when(accountRepository.findByIdWithLock(destId)).thenReturn(Optional.of(dest));
        when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        FinancialTransaction result = transferService.transfer(cmd);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(result.getAmount()).isEqualTo(amount);
        verify(accountRepository, times(2)).save(any());
    }

    @Test void shouldRejectInsufficientFunds() {
        Money amount = Money.of(new BigDecimal("1000.00"), Currency.BRL);
        TransferCommand cmd = new TransferCommand(customerId, sourceId, destId, amount, "key-t002");

        when(transactionRepository.findByIdempotencyKey(any())).thenReturn(Optional.empty());
        when(accountRepository.findByIdWithLock(sourceId)).thenReturn(Optional.of(source));
        when(accountRepository.findByIdWithLock(destId)).thenReturn(Optional.of(dest));

        assertThatThrownBy(() -> transferService.transfer(cmd))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessageContaining("Insufficient funds");
    }

    @Test void shouldRejectSameAccountTransfer() {
        assertThatThrownBy(() -> new TransferCommand(customerId, sourceId, sourceId,
            Money.of(BigDecimal.TEN, Currency.BRL), "key-t003"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test void shouldReturnIdempotentResult() {
        FinancialTransaction existing = FinancialTransaction.builder()
            .id(UUID.randomUUID()).type(com.nexbank.domain.transaction.TransactionType.TRANSFER)
            .sourceAccountId(sourceId).destinationAccountId(destId)
            .amount(Money.of(BigDecimal.TEN, Currency.BRL))
            .status(TransactionStatus.COMPLETED).idempotencyKey("key-t004").createdAt(Instant.now()).build();

        when(transactionRepository.findByIdempotencyKey("key-t004")).thenReturn(Optional.of(existing));
        TransferCommand cmd = new TransferCommand(customerId, sourceId, destId,
            Money.of(BigDecimal.TEN, Currency.BRL), "key-t004");

        assertThat(transferService.transfer(cmd)).isSameAs(existing);
        verify(accountRepository, never()).findByIdWithLock(any());
    }
}
