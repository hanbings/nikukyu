package io.hanbings.nikukyu.server.exception;

public class NoAuthException extends RuntimeException {
    public NoAuthException(String message) {
        super(message);
    }
}