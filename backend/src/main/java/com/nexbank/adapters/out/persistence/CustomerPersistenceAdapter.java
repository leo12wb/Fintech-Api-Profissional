package com.nexbank.adapters.out.persistence;

import com.nexbank.adapters.out.persistence.mapper.CustomerPersistenceMapper;
import com.nexbank.adapters.out.persistence.repository.CustomerJpaRepository;
import com.nexbank.application.customer.port.out.CustomerRepository;
import com.nexbank.application.shared.PagedResult;
import com.nexbank.domain.customer.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @Override
    public PagedResult<Customer> findAll(int page, int size) {
        Page<Customer> result = jpaRepository
            .findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()))
            .map(mapper::toDomain);
        return new PagedResult<>(result.getContent(), result.getNumber(), result.getSize(),
            result.getTotalElements(), result.getTotalPages());
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}
