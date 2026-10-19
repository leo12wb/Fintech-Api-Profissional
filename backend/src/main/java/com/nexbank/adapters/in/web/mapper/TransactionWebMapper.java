package com.nexbank.adapters.in.web.mapper;

import com.nexbank.adapters.in.web.dto.TransactionResponse;
import com.nexbank.domain.transaction.FinancialTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionWebMapper {

    @Mapping(target = "amount", expression = "java(domain.getAmount().getAmount())")
    @Mapping(target = "currency", expression = "java(domain.getAmount().getCurrency())")
    TransactionResponse toResponse(FinancialTransaction domain);
}
