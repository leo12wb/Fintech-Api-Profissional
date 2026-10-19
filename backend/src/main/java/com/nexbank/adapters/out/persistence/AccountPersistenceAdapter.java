package com.nexbank.adapters.out.persistence;

import com.nexbank.adapters.out.persistence.entity.AccountJpaEntity;
import com.nexbank.adapters.out.persistence.entity.CustomerJpaEntity;
import com.nexbank.adapters.out.persistence.mapper.AccountPersistenceMapper;
import com.nexbank.adapters.out.persistence.repository.AccountJpaRepository;
import com.nexbank.adapters.out.persistence.repository.CustomerJpaRepository;
import com.nexbank.application.account.port.out.AccountRepository;
import com.nexbank.domain.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements AccountRepository {

    private final AccountJpaRepository jpaRepository;
    private final CustomerJpaRepository customerJpaRepository;
    private final AccountPersistenceMapper mapper;

    @Override
    public Account save(Account account) {
        AccountJpaEntity entity = mapper.toEntity(account);
        CustomerJpaEntity customerRef = customerJpaRepository.getReferenceById(account.getCustomerId());
        entity.setCustomer(customerRef);
        if (account.getId() != null) {
            entity.setId(account.getId());
        }
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Account> findByIdWithLock(UUID id) {
        return jpaRepository.findByIdWithLock(id).map(mapper::toDomain);
    }

    @Override
    public List<Account> findByCustomerId(UUID customerId) {
        return jpaRepository.findByCustomer_Id(customerId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return jpaRepository.findByAccountNumber(accountNumber).map(mapper::toDomain);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return jpaRepository.existsByAccountNumber(accountNumber);
    }
}
