package com.nexbank.adapters.out.persistence.mapper;

import com.nexbank.adapters.out.persistence.entity.FinancialTransactionJpaEntity;
import com.nexbank.domain.shared.Money;
import com.nexbank.domain.transaction.FinancialTransaction;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TransactionPersistenceMapper {

    @Mapping(target = "amount", expression = "java(Money.ofCents(entity.getAmountCents(), entity.getCurrency()))")
    FinancialTransaction toDomain(FinancialTransactionJpaEntity entity);

    @Mapping(target = "amountCents", expression = "java(domain.getAmount().toCents())")
    @Mapping(target = "currency", expression = "java(domain.getAmount().getCurrency())")
    FinancialTransactionJpaEntity toEntity(FinancialTransaction domain);
}
