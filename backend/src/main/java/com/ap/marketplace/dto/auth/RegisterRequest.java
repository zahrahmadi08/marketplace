package com.ap.marketplace.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Size(min = 6, max = 100) String password,
        @NotBlank @Size(max = 100) String fullName,
        @Email String email,
        @NotBlank @Pattern(regexp = "^09\\d{9}$", message = "شماره موبایل باید با 09 شروع و ۱۱ رقم باشد")
        String phone
) { }
