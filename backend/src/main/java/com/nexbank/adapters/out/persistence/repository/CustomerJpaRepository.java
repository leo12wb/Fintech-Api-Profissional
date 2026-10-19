package com.nexbank.adapters.out.persistence.repository;

import com.nexbank.adapters.out.persistence.entity.CustomerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CustomerJpaRepository extends JpaRepository<CustomerJpaEntity, UUID> {
    Optional<CustomerJpaEntity> findByEmail(String email);
    Optional<CustomerJpaEntity> findByLoginId(String loginId);
    boolean existsByEmail(String email);
}
