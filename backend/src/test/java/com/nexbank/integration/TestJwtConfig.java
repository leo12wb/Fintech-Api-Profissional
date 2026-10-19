package com.nexbank.integration;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@TestConfiguration
public class TestJwtConfig {

    static final KeyPair KEY_PAIR;

    static {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KEY_PAIR = generator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate test RSA key pair", e);
        }
    }

    @Bean
    @Primary
    public RSAPublicKey rsaPublicKey() {
        return (RSAPublicKey) KEY_PAIR.getPublic();
    }

    @Bean
    @Primary
    public RSAPrivateKey rsaPrivateKey() {
        return (RSAPrivateKey) KEY_PAIR.getPrivate();
    }

    @Bean
    @Primary
    public JwtDecoder testJwtDecoder() {
        return NimbusJwtDecoder.withPublicKey((RSAPublicKey) KEY_PAIR.getPublic()).build();
    }

    @Bean
    @Primary
    public JwtEncoder testJwtEncoder() {
        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) KEY_PAIR.getPublic())
            .privateKey((RSAPrivateKey) KEY_PAIR.getPrivate())
            .build();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(rsaKey));
        return new NimbusJwtEncoder(jwkSource);
    }
}
