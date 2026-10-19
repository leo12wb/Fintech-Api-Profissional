package com.nexbank.application.customer.usecase;

import com.nexbank.application.customer.port.in.RegisterCustomerCommand;
import com.nexbank.application.customer.port.in.RegisterCustomerUseCase;
import com.nexbank.application.customer.port.out.CustomerRepository;
import com.nexbank.application.customer.port.out.PasswordEncoder;
import com.nexbank.domain.customer.Customer;
import com.nexbank.domain.customer.CustomerRole;
import com.nexbank.domain.customer.CustomerStatus;
import com.nexbank.domain.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterCustomerService implements RegisterCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Customer register(RegisterCustomerCommand command) {
        String normalizedEmail = Customer.normalizeEmail(command.email());

        if (customerRepository.existsByEmail(normalizedEmail)) {
            throw new BusinessRuleException("EMAIL_ALREADY_EXISTS", "Email already registered: " + normalizedEmail);
        }

        Customer customer = Customer.builder()
            .id(UUID.randomUUID())
            .name(command.name().strip())
            .email(normalizedEmail)
            .loginId(command.loginId().strip().toLowerCase())
            .passwordHash(passwordEncoder.encode(command.rawPassword()))
            .role(CustomerRole.USER)
            .status(CustomerStatus.ACTIVE)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

        Customer saved = customerRepository.save(customer);
        log.info("Customer registered: {}", saved.getId());
        return saved;
    }
}
