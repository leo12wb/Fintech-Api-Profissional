package com.nexbank.adapters.in.web.controller;

import com.nexbank.adapters.in.web.dto.CustomerResponse;
import com.nexbank.adapters.in.web.mapper.CustomerWebMapper;
import com.nexbank.application.customer.port.out.CustomerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/customers/{customerId}")
    @Operation(summary = "Get customer details by ID (ADMIN only)")
    public Optional<CustomerResponse> getCustomer(@PathVariable UUID customerId) {
        return customerRepository.findById(customerId).map(customerMapper::toResponse);
    }
}
