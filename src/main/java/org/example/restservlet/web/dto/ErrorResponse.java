package org.example.restservlet.web.dto;

public class ErrorResponse {

    private String message;

    public ErrorResponse() {
        message = "";
    }

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
