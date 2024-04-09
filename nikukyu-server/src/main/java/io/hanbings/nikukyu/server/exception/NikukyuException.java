package io.hanbings.nikukyu.server.exception;

import io.hanbings.nikukyu.server.data.Message;
import org.springframework.web.bind.annotation.ExceptionHandler;

@SuppressWarnings("SpellCheckingInspection")
public class NikukyuException extends RuntimeException {
    String traceId;
    int code;
    String message;
    long timestamp;

    public NikukyuException(int code, String message, long timestamp) {
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
    }

    @ExceptionHandler(value = NikukyuException.class)
    public Message<?> handleException(NikukyuException e) {
        return null;
    }
}
