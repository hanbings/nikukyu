package io.hanbings.server.nikukyu.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EmailVerifyException extends RuntimeException {
    final int code;
    final String message;
    final Object data;
}
