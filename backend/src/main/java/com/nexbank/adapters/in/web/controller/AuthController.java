package com.nexbank.adapters.in.web.controller;

import com.nexbank.adapters.in.web.dto.AuthResponse;
import com.nexbank.adapters.in.web.dto.CustomerResponse;
import com.nexbank.adapters.in.web.dto.LoginRequest;
import com.nexbank.adapters.in.web.dto.RegisterRequest;
import com.nexbank.adapters.out.security.JwtTokenService;
import com.nexbank.application.customer.port.in.RegisterCustomerCommand;
import com.nexbank.application.customer.port.in.RegisterCustomerUseCase;
import com.nexbank.application.customer.port.out.CustomerRepository;
import com.nexbank.application.customer.port.out.PasswordEncoder;
import com.nexbank.domain.customer.Customer;
import com.nexbank.domain.shared.exception.BusinessRuleException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register and authenticate customers")
public class AuthController {

    private final RegisterCustomerUseCase registerCustomerUseCase;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new customer")
    public CustomerResponse register(@Valid @RequestBody RegisterRequest request) {
        Customer customer = registerCustomerUseCase.register(new RegisterCustomerCommand(
            request.name(), request.email(), request.loginId(), request.password()
        ));
        return new CustomerResponse(
            customer.getId(),
            customer.getName(),
            customer.getEmail(),
            customer.getRole(),
            customer.getStatus(),
            customer.getCreatedAt()
        );
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate and receive JWT tokens")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Customer customer = customerRepository.findByLoginId(request.loginId())
            .orElseThrow(() -> new BusinessRuleException("INVALID_CREDENTIALS", "Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), customer.getPasswordHash())) {
            throw new BusinessRuleException("INVALID_CREDENTIALS", "Invalid credentials");
        }

        if (!customer.isActive()) {
            throw new BusinessRuleException("CUSTOMER_INACTIVE", "Account is not active");
        }

        String accessToken = jwtTokenService.generateAccessToken(customer);
        String refreshToken = jwtTokenService.generateRefreshToken(customer);
        long expiresIn = 3600L;

        return ResponseEntity.ok(new AuthResponse(
            accessToken, refreshToken, expiresIn,
            customer.getId(), customer.getEmail(), customer.getRole().name()
        ));
    }
}
