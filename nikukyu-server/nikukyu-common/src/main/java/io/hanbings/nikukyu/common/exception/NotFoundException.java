package io.hanbings.nikukyu.common.exception;

import io.hanbings.nikukyu.common.data.Message;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotFoundException extends NikukyuException {
    @SuppressWarnings("all")
    public NotFoundException(String traceId, String resource, String path) {
        super(
                traceId,
                Message.ReturnCode.NOT_FOUND,
                Message.Messages.NOT_FOUND
        );

        log.warn(
                """
                        Trace ID: {traceId}
                        {resource} not found in {path}
                        """,
                traceId,
                resource,
                path
        );
    }
}
