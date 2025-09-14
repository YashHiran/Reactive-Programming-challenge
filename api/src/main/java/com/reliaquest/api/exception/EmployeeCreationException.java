package com.reliaquest.api.exception;

public class EmployeeCreationException extends EmployeeApiException {
    public EmployeeCreationException(String message) {
        super("Failed to create employee: " + message);
    }
}
