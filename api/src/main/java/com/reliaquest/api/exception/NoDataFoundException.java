package com.reliaquest.api.exception;
public class NoDataFoundException extends RuntimeException {
    public NoDataFoundException() {
        super("No data found.");
    }

    public NoDataFoundException(String message) {
        super(message);
    }
}
