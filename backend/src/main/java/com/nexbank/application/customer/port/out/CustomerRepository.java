package com.nexbank.application.customer.port.out;

import com.nexbank.application.shared.PagedResult;
import com.nexbank.domain.customer.Customer;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findById(UUID id);
    Optional<Customer> findByEmail(String normalizedEmail);
    Optional<Customer> findByLoginId(String loginId);
    boolean existsByEmail(String normalizedEmail);
    PagedResult<Customer> findAll(int page, int size);
    long count();
}
