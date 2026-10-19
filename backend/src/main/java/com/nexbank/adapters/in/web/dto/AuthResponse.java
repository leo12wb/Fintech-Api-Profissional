package com.nexbank.adapters.in.web.dto;

import java.util.UUID;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn,
    UUID customerId,
    String email,
    String role
) {
    public AuthResponse(String accessToken, String refreshToken, long expiresIn,
                        UUID customerId, String email, String role) {
        this(accessToken, refreshToken, "Bearer", expiresIn, customerId, email, role);
    }
}
