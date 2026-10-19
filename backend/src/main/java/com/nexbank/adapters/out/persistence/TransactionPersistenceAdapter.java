package com.nexbank.adapters.out.persistence;

import com.nexbank.adapters.out.persistence.mapper.TransactionPersistenceMapper;
import com.nexbank.adapters.out.persistence.repository.FinancialTransactionJpaRepository;
import com.nexbank.application.shared.PagedResult;
import com.nexbank.application.transaction.port.out.TransactionRepository;
import com.nexbank.domain.transaction.FinancialTransaction;
import com.nexbank.domain.transaction.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionPersistenceAdapter implements TransactionRepository {

    private final FinancialTransactionJpaRepository jpaRepository;
    private final TransactionPersistenceMapper mapper;

    @Override
    public FinancialTransaction save(FinancialTransaction transaction) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(transaction)));
    }

    @Override
    public Optional<FinancialTransaction> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<FinancialTransaction> findByIdempotencyKey(String key) {
        return jpaRepository.findByIdempotencyKey(key).map(mapper::toDomain);
    }

    @Override
    public PagedResult<FinancialTransaction> findByAccountId(UUID accountId, Instant from, Instant to, TransactionType type, int page, int size) {
        Page<FinancialTransaction> result = jpaRepository
            .findByAccountIdAndFilters(accountId, from, to, type, PageRequest.of(page, size))
            .map(mapper::toDomain);
        return new PagedResult<>(result.getContent(), result.getNumber(), result.getSize(),
            result.getTotalElements(), result.getTotalPages());
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}
