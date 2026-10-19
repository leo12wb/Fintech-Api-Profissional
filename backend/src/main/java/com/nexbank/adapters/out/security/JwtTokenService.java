package com.nexbank.adapters.out.security;

import com.nexbank.adapters.out.configuration.AppProperties;
import com.nexbank.domain.customer.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final AppProperties appProperties;

    public String generateAccessToken(Customer customer) {
        Instant now = Instant.now();
        long expiresIn = appProperties.jwt().expirationMs() / 1000;

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("nexbank")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(expiresIn))
            .subject(customer.getId().toString())
            .claim("email", customer.getEmail())
            .claim("roles", List.of(customer.getRole().name()))
            .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String generateRefreshToken(Customer customer) {
        Instant now = Instant.now();
        long expiresIn = appProperties.jwt().refreshExpirationMs() / 1000;

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("nexbank")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(expiresIn))
            .subject(customer.getId().toString())
            .claim("type", "refresh")
            .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
