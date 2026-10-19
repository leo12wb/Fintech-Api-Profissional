package com.nexbank.adapters.in.web.controller;

import com.nexbank.adapters.in.web.dto.CustomerResponse;
import com.nexbank.adapters.in.web.mapper.CustomerWebMapper;
import com.nexbank.application.customer.port.out.CustomerRepository;
import com.nexbank.application.shared.PagedResult;
import com.nexbank.adapters.out.persistence.repository.AccountJpaRepository;
import com.nexbank.adapters.out.persistence.repository.FinancialTransactionJpaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Administrative operations — requires ADMIN role")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final CustomerRepository customerRepository;
    private final CustomerWebMapper customerMapper;
    private final AccountJpaRepository accountJpaRepository;
    private final FinancialTransactionJpaRepository transactionJpaRepository;

    @GetMapping("/customers")
    @Operation(summary = "List all customers paginated (ADMIN only)")
    public PagedResult<CustomerResponse> listCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResult<com.nexbank.domain.customer.Customer> raw = customerRepository.findAll(page, size);
        return new PagedResult<>(
            raw.content().stream().map(customerMapper::toResponse).toList(),
            raw.pageNumber(), raw.pageSize(), raw.totalElements(), raw.totalPages()
        );
    }

    @GetMapping("/customers/{customerId}")
    @Operation(summary = "Get customer details by ID (ADMIN only)")
    public Optional<CustomerResponse> getCustomer(@PathVariable UUID customerId) {
        return customerRepository.findById(customerId).map(customerMapper::toResponse);
    }

    @GetMapping("/stats")
    @Operation(summary = "Get platform statistics (ADMIN only)")
    public Map<String, Long> getStats() {
        return Map.of(
            "totalCustomers", customerRepository.count(),
            "totalAccounts",  accountJpaRepository.count(),
            "totalTransactions", transactionJpaRepository.count()
        );
    }
}
