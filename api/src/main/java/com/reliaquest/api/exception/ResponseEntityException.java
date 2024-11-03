package com.reliaquest.api.exception;

import org.springframework.http.ResponseEntity;

public class ResponseEntityException extends Exception {
    private final ResponseEntity<?> responseEntity;

    public ResponseEntityException(ResponseEntity<?> responseEntity) {
        super("An error occurred with the provided ResponseEntity");
        this.responseEntity = responseEntity;
    }

    public ResponseEntity<?> getResponseEntity() {
        return responseEntity;
    }
}
