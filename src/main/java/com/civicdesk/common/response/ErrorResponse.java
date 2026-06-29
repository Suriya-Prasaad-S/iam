package com.civicdesk.common.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"status", "message", "path", "timestamp"})
public class ErrorResponse {

    private int status;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String message, String path, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
    }

    public static ErrorResponse of(int status, String message, String path) {
        return new ErrorResponse(status, message, path, LocalDateTime.now());
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
