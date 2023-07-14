package io.hanbings.server.nikukyu.exception;

import io.hanbings.server.nikukyu.data.Message;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ParamsException extends RuntimeException {
    final int code;
    final String message;

    public ParamsException() {
        this.code = Message.ReturnCode.BAD_REQUEST;
        this.message = Message.Messages.BAD_REQUEST;
    }
}
