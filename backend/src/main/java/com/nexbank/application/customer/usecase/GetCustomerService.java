package com.nexbank.application.customer.usecase;
import com.nexbank.application.customer.port.in.GetCustomerUseCase;
import com.nexbank.application.customer.port.out.CustomerRepository;
import com.nexbank.domain.customer.Customer;
import com.nexbank.domain.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCustomerService implements GetCustomerUseCase {
    private final CustomerRepository customerRepository;

    @Override
    @Transactional(readOnly = true)
    public Customer getById(UUID id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new BusinessRuleException("CUSTOMER_NOT_FOUND", "Customer not found: " + id));
    }
}
