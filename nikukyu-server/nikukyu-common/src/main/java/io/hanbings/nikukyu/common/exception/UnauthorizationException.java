package io.hanbings.nikukyu.common.exception;

import io.hanbings.nikukyu.common.data.Message;
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

        log.warn(
                """
                        Trace ID: {traceId}
                        Catch UnauthorizationException: {path}
                        """,
                traceId,
                path
        );
    }
}
