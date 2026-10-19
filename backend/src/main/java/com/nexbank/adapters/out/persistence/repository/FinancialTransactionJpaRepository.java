package com.nexbank.adapters.out.persistence.repository;

import com.nexbank.adapters.out.persistence.entity.FinancialTransactionJpaEntity;
import com.nexbank.domain.transaction.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface FinancialTransactionJpaRepository extends JpaRepository<FinancialTransactionJpaEntity, UUID> {
    Optional<FinancialTransactionJpaEntity> findByIdempotencyKey(String idempotencyKey);

    @Query("""
        SELECT t FROM FinancialTransactionJpaEntity t
        WHERE (t.sourceAccountId = :accountId OR t.destinationAccountId = :accountId)
          AND t.createdAt >= COALESCE(:from, t.createdAt)
          AND t.createdAt <= COALESCE(:to, t.createdAt)
          AND t.type = COALESCE(:type, t.type)
        ORDER BY t.createdAt DESC
        """)
    Page<FinancialTransactionJpaEntity> findByAccountIdAndFilters(
        @Param("accountId") UUID accountId,
        @Param("from") Instant from,
        @Param("to") Instant to,
        @Param("type") TransactionType type,
        Pageable pageable
    );
}
