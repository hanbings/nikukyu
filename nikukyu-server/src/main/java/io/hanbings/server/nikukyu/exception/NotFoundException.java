package io.hanbings.server.nikukyu.exception;

import io.hanbings.server.nikukyu.data.Message;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotFoundException extends RuntimeException {
    final int code;
    final String message;

    public NotFoundException() {
        this.code = Message.ReturnCode.NOT_FOUND;
        this.message = Message.Messages.NOT_FOUND;
    }
}
