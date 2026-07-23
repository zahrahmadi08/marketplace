package com.ap.marketplace.client;

import com.ap.marketplace.client.api.dto.UserDto;

/** حالت نشست کلاینت: توکن و کاربر فعلی. تک‌نمونه ساده. */
public final class Session {

    private static final Session INSTANCE = new Session();
    private String token;
    private UserDto user;

    private Session() { }

    public static Session get() { return INSTANCE; }

    public void login(String token, UserDto user) {
        this.token = token;
        this.user = user;
    }

    public void logout() {
        this.token = null;
        this.user = null;
    }

    public boolean isLoggedIn() { return token != null; }
    public boolean isAdmin() { return user != null && "ADMIN".equals(user.role()); }
    public String token() { return token; }
    public UserDto user() { return user; }
}
