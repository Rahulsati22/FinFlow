package com.rahul.finflow.infrastructure.web.exception;

import java.time.ZonedDateTime;
import java.util.Map;

public record ErrorResponse(
        int status,
        String error,
        String message,
        ZonedDateTime timestamp,
        Map<String, String> validationErrors
) {
    public ErrorResponse(int status, String error, String message){
        this(status, error, message, ZonedDateTime.now(), null);
    }
}
