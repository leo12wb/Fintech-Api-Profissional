package com.nexbank.adapters.in.web.controller;

import com.nexbank.adapters.in.web.dto.DepositRequest;
import com.nexbank.adapters.in.web.dto.TransactionResponse;
import com.nexbank.adapters.in.web.dto.TransferRequest;
import com.nexbank.adapters.in.web.mapper.TransactionWebMapper;
import com.nexbank.application.account.port.out.AccountRepository;
import com.nexbank.application.transaction.port.in.DepositCommand;
import com.nexbank.application.transaction.port.in.DepositUseCase;
import com.nexbank.application.transaction.port.in.TransferCommand;
import com.nexbank.application.transaction.port.in.TransferUseCase;
import com.nexbank.domain.account.Account;
import com.nexbank.domain.shared.Money;
import com.nexbank.domain.shared.exception.BusinessRuleException;
import com.nexbank.domain.transaction.FinancialTransaction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Financial operations — deposits and transfers (simulated)")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final DepositUseCase depositUseCase;
    private final TransferUseCase transferUseCase;
    private final AccountRepository accountRepository;
    private final TransactionWebMapper transactionMapper;

    @PostMapping("/deposits")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Simulate a deposit into an account")
    public TransactionResponse deposit(
            @Valid @RequestBody DepositRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @AuthenticationPrincipal Jwt jwt) {
        UUID customerId = UUID.fromString(jwt.getSubject());
        Money amount = Money.of(request.amount(), request.currency());
        FinancialTransaction tx = depositUseCase.deposit(
            new DepositCommand(customerId, request.accountId(), amount, idempotencyKey));
        return transactionMapper.toResponse(tx);
    }

    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Transfer between two accounts")
    public TransactionResponse transfer(
            @Valid @RequestBody TransferRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @AuthenticationPrincipal Jwt jwt) {
        UUID customerId = UUID.fromString(jwt.getSubject());
        Account destination = accountRepository.findByAccountNumber(request.destinationAccountNumber())
            .orElseThrow(() -> new BusinessRuleException("ACCOUNT_NOT_FOUND",
                "Conta de destino não encontrada: " + request.destinationAccountNumber()));
        Money amount = Money.of(request.amount(), request.currency());
        FinancialTransaction tx = transferUseCase.transfer(
            new TransferCommand(customerId, request.sourceAccountId(), destination.getId(),
                amount, idempotencyKey));
        return transactionMapper.toResponse(tx);
    }
}
