# NexBank — Task Tracker

## Phase 1 — Foundation ✅
- [x] Inspect repository
- [x] Define versions and monorepo structure
- [x] Create README, TASKS.md, .env.example
- [x] Create compose.yaml with all services
- [x] Create backend Spring Boot project skeleton
- [x] Create Flyway migrations (customers, accounts, transactions, outbox, idempotency)
- [x] Create .gitignore and docs structure (ADR-001, ADR-002)
- [x] GitHub Actions CI skeleton
- [x] RSA key generation script

## Phase 2 — Domain & Architecture ✅
- [x] Model domain entities (Customer, Account, FinancialTransaction, Money)
- [x] Define ports (in/out) and use cases (7 use cases)
- [x] Implement financial business rules (debit, credit, insufficient funds, currency checks)
- [x] Implement persistence adapters (4 JPA entities, 4 adapters, 3 MapStruct mappers)
- [x] Write unit tests for domain (MoneyTest, AccountTest)
- [x] Write unit tests for use cases (DepositServiceTest, TransferServiceTest)
- [x] ArchUnit architecture validation tests (5 rules)

## Phase 3 — Security & APIs ✅
- [x] Configure Spring Security + JWT RS256 (RsaKeyConfig, SecurityConfig)
- [x] Implement BCrypt PasswordEncoder adapter (strength 12)
- [x] Implement JWT token service (access + refresh tokens)
- [x] Implement authentication endpoints (register, login)
- [x] Implement account endpoints (create, get, balance)
- [x] Implement transaction endpoints (deposit, transfer with idempotency)
- [x] GlobalExceptionHandler with ProblemDetail (RFC 9457)
- [x] CorrelationId filter (MDC X-Correlation-Id header)
- [x] Integration tests with Testcontainers (AuthIntegrationTest, AccountIntegrationTest)

## Phase 4 — Frontend ✅
- [x] Bootstrap Angular 18 standalone project with Angular Material 18
- [x] Configure Angular routing with lazy loading and guards (authGuard, adminGuard)
- [x] Configure HTTP interceptors (JWT bearer, auto-logout on 401)
- [x] Implement authentication screens (login, register)
- [x] Implement dashboard (balance card, accounts grid, recent transactions)
- [x] Implement account management (list, detail, open account)
- [x] Implement transaction flows (deposit, transfer with confirm dialog)
- [x] Implement statement view (paginated table, status chips)
- [x] Admin dashboard (ADMIN role only)
- [x] Responsive design with Angular Material theme (Indigo/Teal)
- [x] Docker multi-stage build + nginx SPA config

## Phase 5 — Messaging & Observability ✅
- [x] RabbitMQ topology (TopicExchange, queues, DLQ, bindings, Jackson converter)
- [x] Outbox Poller (@Scheduled, batch=50, retry=3, PROCESSED/FAILED status)
- [x] Notification Consumer (@RabbitListener for TransactionCompleted, AccountCreated)
- [x] Custom Micrometer metrics (nexbank_transactions_total, nexbank_transaction_amount_cents)
- [x] Custom health indicator (OutboxHealthIndicator — UP/DEGRADED/DOWN)
- [x] Structured logging with correlation IDs (MDC pattern in log output)
- [x] Fix ArchUnit violation: TokenService port decouples AuthController from JwtTokenService
- [x] Fix MapStruct imports in persistence mappers

## Phase 6 — CI/CD & Polish ✅
- [x] GitHub Actions CI: backend with Testcontainers (no external service containers)
- [x] GitHub Actions CI: frontend build + lint
- [x] JaCoCo code coverage plugin + HTML report artifact
- [x] Docker Compose: RabbitMQ data volume, clean configuration
- [x] pom.xml: version 1.0.0
- [x] README: complete with architecture, endpoints, metrics, local dev guide
- [x] TASKS.md: final tracker update
