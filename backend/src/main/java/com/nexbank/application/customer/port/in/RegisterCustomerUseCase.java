package com.nexbank.application.customer.port.in;

import com.nexbank.domain.customer.Customer;

public interface RegisterCustomerUseCase {
    Customer register(RegisterCustomerCommand command);
}
