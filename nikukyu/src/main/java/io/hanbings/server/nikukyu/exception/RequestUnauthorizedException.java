package io.hanbings.server.nikukyu.exception;

public class RequestUnauthorizedException extends RuntimeException {
    public RequestUnauthorizedException(String message) {
        super(message);
    }
}
