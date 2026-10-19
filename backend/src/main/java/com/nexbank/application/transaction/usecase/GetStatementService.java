package com.nexbank.application.transaction.usecase;

import com.nexbank.application.account.port.out.AccountRepository;
import com.nexbank.application.shared.PagedResult;
import com.nexbank.application.transaction.port.in.GetStatementQuery;
import com.nexbank.application.transaction.port.in.GetStatementUseCase;
import com.nexbank.application.transaction.port.out.TransactionRepository;
import com.nexbank.domain.account.Account;
import com.nexbank.domain.shared.exception.BusinessRuleException;
import com.nexbank.domain.transaction.FinancialTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetStatementService implements GetStatementUseCase {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResult<FinancialTransaction> getStatement(GetStatementQuery query) {
        Account account = accountRepository.findById(query.accountId())
            .orElseThrow(() -> new BusinessRuleException("ACCOUNT_NOT_FOUND", "Account not found: " + query.accountId()));
        if (!account.belongsTo(query.customerId())) {
            throw new BusinessRuleException("ACCESS_DENIED", "Access denied to account: " + query.accountId());
        }
        return transactionRepository.findByAccountId(
            query.accountId(), query.from(), query.to(), query.type(), query.page(), query.size());
    }
}
