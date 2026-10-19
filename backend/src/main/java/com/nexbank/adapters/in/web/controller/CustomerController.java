package com.nexbank.adapters.in.web.controller;

import com.nexbank.adapters.in.web.dto.CustomerResponse;
import com.nexbank.adapters.in.web.mapper.CustomerWebMapper;
import com.nexbank.application.customer.port.in.GetCustomerUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer profile operations")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    private final GetCustomerUseCase getCustomerUseCase;
    private final CustomerWebMapper customerMapper;

    @GetMapping("/me")
    @Operation(summary = "Get authenticated customer profile")
    public CustomerResponse getMe(@AuthenticationPrincipal Jwt jwt) {
        UUID customerId = UUID.fromString(jwt.getSubject());
        return customerMapper.toResponse(getCustomerUseCase.getById(customerId));
    }
}
