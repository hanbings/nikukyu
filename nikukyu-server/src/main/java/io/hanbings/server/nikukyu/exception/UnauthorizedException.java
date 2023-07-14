package io.hanbings.server.nikukyu.exception;

import io.hanbings.server.nikukyu.data.Message;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UnauthorizedException extends RuntimeException {
    final int code;
    final String message;

    public UnauthorizedException() {
        this.code = Message.ReturnCode.UNAUTHORIZED;
        this.message = Message.Messages.UNAUTHORIZED;
    }
}
