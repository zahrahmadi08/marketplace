package com.ap.marketplace.dto.auth;

import com.ap.marketplace.domain.User;

public record UserResponse(
        Long id, String username, String fullName, String email,
        String phone, String role, String status
) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getUsername(), u.getFullName(), u.getEmail(),
                u.getPhone(), u.getRole().name(), u.getStatus().name());
    }
}
