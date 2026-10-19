package com.nexbank.application.authentication.port.out;

import com.nexbank.domain.customer.Customer;

public interface TokenService {
    String generateAccessToken(Customer customer);
    String generateRefreshToken(Customer customer);
}
