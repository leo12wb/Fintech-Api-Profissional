package com.nexbank.integration;

import com.nexbank.adapters.in.web.dto.AuthResponse;
import com.nexbank.adapters.in.web.dto.CustomerResponse;
import com.nexbank.adapters.in.web.dto.LoginRequest;
import com.nexbank.adapters.in.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class AuthIntegrationTest extends BaseIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldRegisterAndLoginSuccessfully() {
        RegisterRequest registerReq = new RegisterRequest(
            "Test User", "test@nexbank.com", "testuser", "Password1!");

        ResponseEntity<CustomerResponse> registerResp = restTemplate.postForEntity(
            "/api/v1/auth/register", registerReq, CustomerResponse.class);

        assertThat(registerResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(registerResp.getBody()).isNotNull();
        assertThat(registerResp.getBody().email()).isEqualTo("test@nexbank.com");

        LoginRequest loginReq = new LoginRequest("testuser", "Password1!");
        ResponseEntity<AuthResponse> loginResp = restTemplate.postForEntity(
            "/api/v1/auth/login", loginReq, AuthResponse.class);

        assertThat(loginResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResp.getBody()).isNotNull();
        assertThat(loginResp.getBody().accessToken()).isNotBlank();
    }

    @Test
    void shouldRejectDuplicateEmail() {
        RegisterRequest req = new RegisterRequest(
            "Dup User", "dup@nexbank.com", "dupuser", "Password1!");
        restTemplate.postForEntity("/api/v1/auth/register", req, CustomerResponse.class);

        RegisterRequest req2 = new RegisterRequest(
            "Dup User 2", "dup@nexbank.com", "dupuser2", "Password1!");
        ResponseEntity<String> resp = restTemplate.postForEntity(
            "/api/v1/auth/register", req2, String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void shouldRejectInvalidCredentials() {
        LoginRequest req = new LoginRequest("nonexistent", "wrongpass");
        ResponseEntity<String> resp = restTemplate.postForEntity(
            "/api/v1/auth/login", req, String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
