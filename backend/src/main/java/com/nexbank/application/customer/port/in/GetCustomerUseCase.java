package com.nexbank.application.customer.port.in;
import com.nexbank.domain.customer.Customer;
import java.util.UUID;
public interface GetCustomerUseCase {
    Customer getById(UUID id);
}
