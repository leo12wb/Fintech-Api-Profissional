package com.nexbank.adapters.out.persistence.mapper;

import com.nexbank.adapters.out.persistence.entity.AccountJpaEntity;
import com.nexbank.domain.account.Account;
import com.nexbank.domain.account.AccountNumber;
import com.nexbank.domain.shared.Money;
import org.mapstruct.*;

@Mapper(componentModel = "spring", imports = {AccountNumber.class, Money.class})
public interface AccountPersistenceMapper {

    @Mapping(target = "accountNumber", expression = "java(AccountNumber.of(entity.getAccountNumber()))")
    @Mapping(target = "customerId", expression = "java(entity.getCustomer().getId())")
    @Mapping(target = "balance", expression = "java(Money.ofCents(entity.getBalanceCents(), entity.getCurrency()))")
    Account toDomain(AccountJpaEntity entity);

    @Mapping(target = "accountNumber", expression = "java(domain.getAccountNumber().getValue())")
    @Mapping(target = "customer", ignore = true)  // set in adapter
    @Mapping(target = "balanceCents", expression = "java(domain.getBalance().toCents())")
    AccountJpaEntity toEntity(Account domain);
}
