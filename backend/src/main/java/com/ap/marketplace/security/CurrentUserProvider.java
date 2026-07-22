package com.ap.marketplace.security;

import com.ap.marketplace.domain.User;
import com.ap.marketplace.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** کاربر احرازشده جاری را از SecurityContext برمی‌گرداند. */
@Component
public class CurrentUserProvider {

    public User require() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AppUserDetails details)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "احراز هویت لازم است");
        }
        return details.getUser();
    }
}
