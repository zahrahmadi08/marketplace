package com.ap.marketplace.client.api;

/** خطای برگشتی از سرور با پیام قابل‌نمایش. */
public class ApiClientException extends RuntimeException {
    private final int status;

    public ApiClientException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() { return status; }
}
