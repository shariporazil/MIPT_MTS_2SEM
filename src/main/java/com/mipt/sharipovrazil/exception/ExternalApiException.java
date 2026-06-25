package com.mipt.sharipovrazil.exception;

import org.springframework.http.HttpStatusCode;

public class ExternalApiException extends RuntimeException {
    private final HttpStatusCode statusCode;

    public ExternalApiException(String message) {
        this(message, null, null);
    }

    public ExternalApiException(String message, HttpStatusCode statusCode) {
        this(message, statusCode, null);
    }

    public ExternalApiException(String message, Throwable cause) {
        this(message, null, cause);
    }

    public ExternalApiException(String message, HttpStatusCode statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}