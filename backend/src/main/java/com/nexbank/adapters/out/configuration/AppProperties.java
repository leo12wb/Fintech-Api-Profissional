package com.nexbank.adapters.out.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "nexbank")
public record AppProperties(CorsProperties cors, JwtConfig jwt) {

    public record CorsProperties(List<String> allowedOrigins) {}

    public record JwtConfig(long expirationMs, long refreshExpirationMs) {}
}
