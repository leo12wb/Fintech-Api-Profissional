package com.nexbank.adapters.out.persistence.mapper;

import com.nexbank.adapters.out.persistence.entity.CustomerJpaEntity;
import com.nexbank.domain.customer.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerPersistenceMapper {
    @Mapping(source = "loginId", target = "loginId")
    @Mapping(source = "passwordHash", target = "passwordHash")
    Customer toDomain(CustomerJpaEntity entity);

    CustomerJpaEntity toEntity(Customer domain);
}
