# ADR-001: Hexagonal Architecture (Ports and Adapters)

**Status:** Accepted
**Date:** 2024-01-01
**Deciders:** NexBank Engineering

---

## Context

We need to choose an architectural style for the NexBank backend that:

1. Keeps the domain model free of framework dependencies so that business logic can be tested without Spring, JPA, or any infrastructure concern.
2. Makes it easy to swap infrastructure components (e.g., replace PostgreSQL with another database, or replace RabbitMQ with Kafka) without touching business logic.
3. Enforces a clear separation of concerns that guides contributors toward the correct layer.
4. Provides a structure that is widely understood and documented in the industry.

Alternative architectures considered: Layered (Traditional N-Tier), Clean Architecture (Uncle Bob), CQRS-only flat packages.

---

## Decision

We will adopt **Hexagonal Architecture** (also known as Ports and Adapters, coined by Alistair Cockburn).

### Package structure

```
com.nexbank/
├── domain/                    # The hexagon core — pure Java, zero framework deps
│   ├── account/               # Account aggregate, value objects, domain events
│   ├── customer/              # Customer aggregate
│   ├── transaction/           # FinancialTransaction aggregate
│   └── shared/                # Shared kernel (Money, AuditInfo, DomainEvent)
│
├── application/               # Use cases — orchestrates domain, depends on ports
│   ├── account/
│   │   ├── usecase/           # AccountService implementations
│   │   ├── port/in/           # Input ports (interfaces the driving adapters call)
│   │   └── port/out/          # Output ports (interfaces the driven adapters implement)
│   ├── transaction/
│   ├── authentication/
│   └── notification/
│
└── adapters/                  # Outside the hexagon — all framework code lives here
    ├── in/                    # Driving adapters (REST, messaging listeners)
    │   ├── web/
    │   │   ├── controller/
    │   │   ├── dto/
    │   │   └── mapper/
    │   └── messaging/
    └── out/                   # Driven adapters (JPA, AMQP publishers, JWT)
        ├── persistence/
        │   ├── entity/
        │   ├── repository/
        │   └── mapper/
        ├── messaging/
        ├── security/
        └── configuration/    # Spring @Configuration classes
```

### Dependency rules

- `domain` has **no** dependency on `application` or `adapters`.
- `application` depends on `domain` only.
- `adapters` depend on `application` (through ports) and on `domain`.
- Spring annotations (`@Component`, `@Service`, `@Repository`, etc.) are **not** used inside `domain` or on use-case interfaces in `port/in` and `port/out`.

### Enforcement

ArchUnit tests (Phase 2) will codify these rules and run as part of the CI pipeline.

---

## Consequences

**Positive:**
- Domain logic is 100% unit-testable without spinning up Spring.
- Infrastructure changes (e.g., adding a Redis cache) are localised to the adapters layer.
- New developers have clear rules about where to put code.
- Aligns with the industry's trend toward testability-first backends.

**Negative:**
- More files and indirection than a simple layered architecture — may feel over-engineered for small features.
- Requires discipline: it is easy to "accidentally" put a JPA annotation on a domain entity.
- Mapper boilerplate between domain objects and JPA entities (mitigated by MapStruct).
