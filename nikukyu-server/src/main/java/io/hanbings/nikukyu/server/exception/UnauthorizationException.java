package io.hanbings.nikukyu.server.exception;

import io.hanbings.nikukyu.server.data.Message;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("SpellCheckingInspection")
public class UnauthorizationException extends NikukyuException {
    @SuppressWarnings("all")
    public UnauthorizationException(String traceId, String path) {
        super(
                traceId,
                Message.ReturnCode.UNAUTHORIZED,
                Message.Messages.UNAUTHORIZED
        );

        log.warn(STR."Trace ID: \{traceId} Catch UnauthorizationException: \{path}");
    }
}
