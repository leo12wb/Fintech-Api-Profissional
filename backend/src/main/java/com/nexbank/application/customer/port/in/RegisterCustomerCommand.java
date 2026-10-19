package com.nexbank.application.customer.port.in;

public record RegisterCustomerCommand(String name, String email, String loginId, String rawPassword) {
    public RegisterCustomerCommand {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name required");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("email required");
        if (loginId == null || loginId.isBlank()) throw new IllegalArgumentException("loginId required");
        if (rawPassword == null || rawPassword.length() < 8) throw new IllegalArgumentException("password min 8 chars");
    }
}
