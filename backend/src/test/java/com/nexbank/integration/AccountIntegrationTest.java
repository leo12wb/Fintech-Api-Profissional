package com.nexbank.integration;

import com.nexbank.adapters.in.web.dto.AccountResponse;
import com.nexbank.adapters.in.web.dto.AuthResponse;
import com.nexbank.adapters.in.web.dto.CustomerResponse;
import com.nexbank.adapters.in.web.dto.LoginRequest;
import com.nexbank.adapters.in.web.dto.OpenAccountRequest;
import com.nexbank.adapters.in.web.dto.RegisterRequest;
import com.nexbank.domain.shared.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AccountIntegrationTest extends BaseIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    private String accessToken;

    @BeforeEach
    void setup() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        RegisterRequest reg = new RegisterRequest(
            "Account Test", "acct" + suffix + "@nexbank.com",
            "acctuser" + suffix, "Password1!");
        restTemplate.postForEntity("/api/v1/auth/register", reg, CustomerResponse.class);

        LoginRequest login = new LoginRequest("acctuser" + suffix, "Password1!");
        AuthResponse auth = restTemplate.postForEntity(
            "/api/v1/auth/login", login, AuthResponse.class).getBody();
        assertThat(auth).isNotNull();
        accessToken = auth.accessToken();
    }

    private HttpHeaders authHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(accessToken);
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    @Test
    void shouldOpenAndRetrieveAccount() {
        OpenAccountRequest req = new OpenAccountRequest(Currency.BRL);
        ResponseEntity<AccountResponse> created = restTemplate.exchange(
            "/api/v1/accounts", HttpMethod.POST,
            new HttpEntity<>(req, authHeaders()), AccountResponse.class);

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();
        assertThat(created.getBody().currency()).isEqualTo(Currency.BRL);

        ResponseEntity<AccountResponse> fetched = restTemplate.exchange(
            "/api/v1/accounts/" + created.getBody().id(), HttpMethod.GET,
            new HttpEntity<>(authHeaders()), AccountResponse.class);

        assertThat(fetched.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldRejectUnauthorizedAccountAccess() {
        ResponseEntity<String> resp = restTemplate.getForEntity("/api/v1/accounts", String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
