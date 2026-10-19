package com.nexbank.adapters.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank @Size(min = 2, max = 150) String name,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 3, max = 100) String loginId,
    @NotBlank @Size(min = 8, max = 100) String password
) {}
