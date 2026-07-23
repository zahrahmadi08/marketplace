package com.ap.marketplace.client.api;

import com.ap.marketplace.client.api.dto.AuthResponse;
import java.util.LinkedHashMap;
import java.util.Map;

public final class AuthApi {
    private AuthApi() { }

    public static AuthResponse login(String username, String password) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("username", username);
        body.put("password", password);
        return ApiClient.post("/api/auth/login", body, AuthResponse.class);
    }

    public static AuthResponse register(String username, String password, String fullName,
                                        String email, String phone) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("username", username);
        body.put("password", password);
        body.put("fullName", fullName);
        body.put("email", email);
        body.put("phone", phone);
        return ApiClient.post("/api/auth/register", body, AuthResponse.class);
    }
}
