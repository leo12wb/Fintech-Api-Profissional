package com.nexbank.adapters.in.web.mapper;

import com.nexbank.adapters.in.web.dto.CustomerResponse;
import com.nexbank.domain.customer.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerWebMapper {

    CustomerResponse toResponse(Customer domain);
}
