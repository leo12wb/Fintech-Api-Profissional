package com.nexbank.adapters.in.web.mapper;

import com.nexbank.adapters.in.web.dto.AccountResponse;
import com.nexbank.domain.account.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountWebMapper {

    @Mapping(target = "accountNumber", expression = "java(domain.getAccountNumber().getValue())")
    @Mapping(target = "balance", expression = "java(domain.getBalance().getAmount())")
    AccountResponse toResponse(Account domain);
}
