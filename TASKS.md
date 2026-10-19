# NexBank — Task Tracker

## Phase 1 — Foundation ✅
- [x] Inspect repository
- [x] Define versions and monorepo structure
- [x] Create README, TASKS.md, .env.example
- [x] Create compose.yaml with all services
- [x] Create backend Spring Boot project skeleton
- [x] Create Flyway migrations
- [x] Create .gitignore and docs structure

## Phase 2 — Domain & Architecture ⏳
- [ ] Model domain entities (Customer, Account, FinancialTransaction)
- [ ] Define ports (in/out) and use cases
- [ ] Implement financial business rules
- [ ] Implement persistence adapters
- [ ] Write unit tests for domain
- [ ] ArchUnit architecture validation tests

## Phase 3 — Security & APIs ⏳
- [ ] Configure Spring Security + JWT RS256
- [ ] Implement authentication use case (login / refresh)
- [ ] Implement customer registration endpoint
- [ ] Implement account endpoints (create, get balance)
- [ ] Implement transaction endpoints (deposit, transfer)
- [ ] Implement idempotency key filter
- [ ] Integration tests with Testcontainers

## Phase 4 — Frontend ⏳
- [ ] Bootstrap Angular 18 project
- [ ] Configure Angular routing and HTTP interceptors
- [ ] Implement authentication screens (login / register)
- [ ] Implement dashboard (account balance, recent transactions)
- [ ] Implement transfer flow
- [ ] Unit tests with Jest / Karma

## Phase 5 — Messaging & Observability ⏳
- [ ] Implement Transactional Outbox publisher
- [ ] Implement RabbitMQ event consumers
- [ ] Configure Prometheus scrape and Grafana dashboards
- [ ] Add structured logging with correlation IDs
- [ ] Distributed tracing (Micrometer Tracing / Zipkin)

## Phase 6 — CI/CD & Polish ⏳
- [ ] GitHub Actions: backend build + test
- [ ] GitHub Actions: frontend build + lint
- [ ] Docker multi-stage builds optimised
- [ ] Security hardening (OWASP headers, rate limiting)
- [ ] Final README polish and architecture diagrams
- [ ] Record demo video / screenshots
