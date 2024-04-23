package org.example.restservlet.exception;

public class SqlPerformingException extends RuntimeException{
    public SqlPerformingException(String message) {
        super(message);
    }
}
