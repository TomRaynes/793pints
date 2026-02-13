package com.pints793.rest.controller;

import org.springframework.http.HttpStatus;

public class ErrorResponseException extends Exception {

    private HttpStatus status;

    public ErrorResponseException(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
