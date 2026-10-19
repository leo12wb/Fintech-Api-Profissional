package com.nexbank.adapters.out.persistence;

import com.nexbank.adapters.out.persistence.mapper.CustomerPersistenceMapper;
import com.nexbank.adapters.out.persistence.repository.CustomerJpaRepository;
import com.nexbank.application.customer.port.out.CustomerRepository;
import com.nexbank.domain.customer.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerPersistenceAdapter implements CustomerRepository {

    private final CustomerJpaRepository jpaRepository;
    private final CustomerPersistenceMapper mapper;

    @Override
    public Customer save(Customer customer) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(customer)));
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Customer> findByEmail(String normalizedEmail) {
        return jpaRepository.findByEmail(normalizedEmail).map(mapper::toDomain);
    }

    @Override
    public Optional<Customer> findByLoginId(String loginId) {
        return jpaRepository.findByLoginId(loginId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String normalizedEmail) {
        return jpaRepository.existsByEmail(normalizedEmail);
    }
}
