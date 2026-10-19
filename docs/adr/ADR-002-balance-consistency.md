# ADR-002: Balance Consistency Strategy — Ledger + Persistent Balance

**Status:** Accepted
**Date:** 2024-01-01
**Deciders:** NexBank Engineering

---

## Context

Financial systems must answer two competing questions efficiently:

1. **"What is this account's current balance?"** — needs to be fast (O(1)), used on every dashboard load and transfer validation.
2. **"What is the full audit trail of every cent that moved?"** — needs to be complete and immutable, used for reconciliation and compliance.

Two common strategies:

| Strategy | Description | Pros | Cons |
|---|---|---|---|
| **Pure ledger** | Balance is always `SUM(amount)` over all transactions | Immutable, auditable | Slow for large accounts; requires aggregate query on every read |
| **Stored balance + ledger** | Balance stored on account row AND immutable ledger entries | Fast reads, full audit trail | Consistency must be enforced; two things to keep in sync |

---

## Decision

We will use a **hybrid strategy: persistent `balance_cents` on the `accounts` table combined with an immutable ledger in `financial_transactions`**.

### Rules

1. **`balance_cents` on `accounts`** is the authoritative, fast-read balance. It is always up-to-date.
2. **`financial_transactions`** entries are append-only and immutable. They form the complete audit trail.
3. Both the balance update and the ledger entry are written in **the same database transaction**, guaranteeing they are always consistent.
4. The `version` column on `accounts` enables **optimistic locking** (via JPA `@Version`). If two concurrent transfers attempt to debit the same account simultaneously, one will receive an `OptimisticLockException` and be retried at the application layer.
5. A `CHECK CONSTRAINT` (`balance_cents >= 0`) at the database level acts as a final safety net against negative balances.

### Sequence for a transfer

```
BEGIN TRANSACTION
  SELECT * FROM accounts WHERE id = :source FOR UPDATE  -- pessimistic on source only
  SELECT * FROM accounts WHERE id = :dest               -- version read for OL
  -- validate: source balance >= amount
  UPDATE accounts SET balance_cents = balance_cents - :amount, version = version + 1 WHERE id = :source AND version = :expected_version
  UPDATE accounts SET balance_cents = balance_cents + :amount, version = version + 1 WHERE id = :dest AND version = :expected_version
  INSERT INTO financial_transactions (...) VALUES (...)
  INSERT INTO outbox_events (...) VALUES (...)          -- Transactional Outbox
COMMIT
```

### Idempotency

All write endpoints accept an `Idempotency-Key` header. The key and its response are stored in `idempotency_keys`. If the same key is replayed, the stored response is returned without re-executing the transaction.

---

## Consequences

**Positive:**
- `GET /accounts/{id}/balance` is a single indexed row lookup — O(1).
- Full immutable ledger available for auditing.
- Optimistic locking prevents lost-update anomalies under concurrent load.
- Database constraint enforces non-negative balance even if application logic has a bug.

**Negative:**
- Two writes per transaction (balance update + ledger insert) inside the same DB transaction — slightly more work per write than a pure ledger.
- `OptimisticLockException` retry logic must be implemented in the application layer.
- If the constraint `balance_cents >= 0` fires, it surfaces as a database error that must be caught and translated into a domain exception.
