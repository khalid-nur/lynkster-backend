package com.lynkster.backend.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private boolean success;
    private String message;
    private int statusCode;
    private String instance;
    private Instant timestamp;
    private Map<String, String> errors;

    public static ErrorResponse response(String message, int statusCode, String instance, Map<String, String> errors
    ) {
        return new ErrorResponse(
                false,
                message,
                statusCode,
                instance,
                Instant.now(),
                errors
        );
    }
}

