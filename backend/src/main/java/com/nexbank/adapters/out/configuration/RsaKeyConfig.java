package com.nexbank.adapters.out.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * Provides RSA key beans for JWT signing/verification.
 * Both beans are marked {@code @ConditionalOnMissingBean} so that test configurations
 * (e.g. {@code TestJwtConfig}) can supply in-memory generated keys without requiring
 * PEM files on the classpath.
 */
@Configuration
public class RsaKeyConfig {

    @Bean
    @ConditionalOnMissingBean
    public RSAPublicKey rsaPublicKey(
            @Value("${spring.security.oauth2.resourceserver.jwt.public-key-location}") RSAPublicKey key) {
        return key;
    }

    @Bean
    @ConditionalOnMissingBean
    public RSAPrivateKey rsaPrivateKey(
            @Value("${nexbank.jwt.private-key-location:classpath:certs/private.pem}") RSAPrivateKey key) {
        return key;
    }
}
