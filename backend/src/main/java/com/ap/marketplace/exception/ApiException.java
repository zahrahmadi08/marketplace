package com.ap.marketplace.exception;

import org.springframework.http.HttpStatus;

/** پایه خطاهای دامنه با کد HTTP همراه. */
public class ApiException extends RuntimeException {
    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() { return status; }
}
