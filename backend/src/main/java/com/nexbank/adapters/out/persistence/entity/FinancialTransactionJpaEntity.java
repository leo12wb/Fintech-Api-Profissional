package com.nexbank.adapters.out.persistence.entity;

import com.nexbank.domain.shared.Currency;
import com.nexbank.domain.transaction.TransactionStatus;
import com.nexbank.domain.transaction.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "financial_transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FinancialTransactionJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Column(name = "source_account_id")
    private UUID sourceAccountId;

    @Column(name = "destination_account_id")
    private UUID destinationAccountId;

    @Column(name = "amount_cents", nullable = false)
    private Long amountCents;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionStatus status;

    @Column(name = "idempotency_key", length = 255, unique = true)
    private String idempotencyKey;

    @Column(name = "business_reference", length = 255)
    private String businessReference;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
