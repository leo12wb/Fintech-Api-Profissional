package com.nexbank.application.account.usecase;

import com.nexbank.application.account.port.in.GetAccountUseCase;
import com.nexbank.application.account.port.out.AccountRepository;
import com.nexbank.domain.account.Account;
import com.nexbank.domain.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetAccountService implements GetAccountUseCase {

    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public Account getAccountById(UUID accountId, UUID requestingCustomerId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new BusinessRuleException("ACCOUNT_NOT_FOUND", "Account not found: " + accountId));
        if (!account.belongsTo(requestingCustomerId)) {
            throw new BusinessRuleException("ACCESS_DENIED", "Access denied to account: " + accountId);
        }
        return account;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> getAccountsByCustomer(UUID customerId) {
        return accountRepository.findByCustomerId(customerId);
    }
}
