package io.hanbings.server.nikukyu.exception;

import io.hanbings.server.nikukyu.data.Message;
import lombok.Getter;

@Getter
public class ControllerException extends Exception {
    final int code;
    final String message;
    final Object data;

    public <T> ControllerException(int code, String message, T data) {
        super(message);

        this.code = code;
        this.message = message;
        this.data = data;
    }

    public <T> ControllerException(Message<T> message) {
        super(message.getMessage());

        this.code = message.getCode();
        this.message = message.getMessage();
        this.data = message.getData();
    }
}
