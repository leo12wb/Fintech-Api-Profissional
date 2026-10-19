package com.nexbank.adapters.in.web.controller;

import com.nexbank.adapters.in.web.dto.AccountResponse;
import com.nexbank.adapters.in.web.dto.BalanceResponse;
import com.nexbank.adapters.in.web.dto.OpenAccountRequest;
import com.nexbank.adapters.in.web.dto.StatementResponse;
import com.nexbank.adapters.in.web.dto.TransactionResponse;
import com.nexbank.adapters.in.web.mapper.AccountWebMapper;
import com.nexbank.adapters.in.web.mapper.TransactionWebMapper;
import com.nexbank.application.account.port.in.GetAccountUseCase;
import com.nexbank.application.account.port.in.OpenAccountCommand;
import com.nexbank.application.account.port.in.OpenAccountUseCase;
import com.nexbank.application.shared.PagedResult;
import com.nexbank.application.transaction.port.in.GetStatementQuery;
import com.nexbank.application.transaction.port.in.GetStatementUseCase;
import com.nexbank.domain.account.Account;
import com.nexbank.domain.transaction.FinancialTransaction;
import com.nexbank.domain.transaction.TransactionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Manage digital bank accounts")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

    private final OpenAccountUseCase openAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final GetStatementUseCase getStatementUseCase;
    private final AccountWebMapper accountMapper;
    private final TransactionWebMapper transactionMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Open a new account")
    public AccountResponse openAccount(@Valid @RequestBody OpenAccountRequest request,
                                       @AuthenticationPrincipal Jwt jwt) {
        UUID customerId = UUID.fromString(jwt.getSubject());
        Account account = openAccountUseCase.openAccount(new OpenAccountCommand(customerId, request.currency()));
        return accountMapper.toResponse(account);
    }

    @GetMapping
    @Operation(summary = "List all accounts for the authenticated customer")
    public List<AccountResponse> listAccounts(@AuthenticationPrincipal Jwt jwt) {
        UUID customerId = UUID.fromString(jwt.getSubject());
        return getAccountUseCase.getAccountsByCustomer(customerId).stream()
            .map(accountMapper::toResponse)
            .toList();
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get account details")
    public AccountResponse getAccount(@PathVariable UUID accountId,
                                      @AuthenticationPrincipal Jwt jwt) {
        UUID customerId = UUID.fromString(jwt.getSubject());
        return accountMapper.toResponse(getAccountUseCase.getAccountById(accountId, customerId));
    }

    @GetMapping("/{accountId}/balance")
    @Operation(summary = "Get account balance")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable UUID accountId,
                                                      @AuthenticationPrincipal Jwt jwt) {
        UUID customerId = UUID.fromString(jwt.getSubject());
        Account account = getAccountUseCase.getAccountById(accountId, customerId);
        return ResponseEntity.ok(new BalanceResponse(
            account.getAccountNumber().getValue(),
            account.getBalance().getAmount(),
            account.getBalance().getCurrency(),
            account.getStatus()
        ));
    }

    @GetMapping("/{accountId}/statement")
    @Operation(summary = "Get account statement")
    public StatementResponse getStatement(
            @PathVariable UUID accountId,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal Jwt jwt) {
        UUID customerId = UUID.fromString(jwt.getSubject());
        PagedResult<FinancialTransaction> result = getStatementUseCase.getStatement(
            new GetStatementQuery(customerId, accountId, from, to, type, page, Math.min(size, 100)));
        List<TransactionResponse> txs = result.content().stream()
            .map(transactionMapper::toResponse)
            .toList();
        return new StatementResponse(txs, result.pageNumber(), result.pageSize(),
            result.totalElements(), result.totalPages());
    }
}
