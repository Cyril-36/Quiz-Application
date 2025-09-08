package com.cyril.quiz;

public class ApiException extends Exception {
    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}