package com.ejemplo.PruebaAPISpringBoot.exception;

import java.time.Instant;

public class ErrorResponse {
    private Instant timestamp = Instant.now();
    private int status;
    private String error;
    private String message;

    public ErrorResponse() {}
    public ErrorResponse(int status, String error, String message) {
        this.status = status; this.error = error; this.message = message;
        this.timestamp = Instant.now();
    }
    public Instant getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public void setStatus(int status) { this.status = status; }
    public void setError(String error) { this.error = error; }
    public void setMessage(String message) { this.message = message; }
}