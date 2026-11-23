package com.lynkster.backend.common.response;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private int statusCode;
    private Object meta;

    public static <T> ApiResponse<T> response( boolean success, String message, T data, int statusCode) {
        return new ApiResponse<>( success, message, data, statusCode, null);
    }

    public static <T> ApiResponse<T> response( boolean success, String message, T data, int statusCode, Object meta) {
        return new ApiResponse<>( success, message, data, statusCode, meta);
    }
}

