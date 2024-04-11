package io.hanbings.nikukyu.server.exception;

import io.hanbings.nikukyu.server.data.Message;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotFoundException extends NikukyuException {
    public NotFoundException(String traceId, String resource, String path) {
        super(
                traceId,
                Message.ReturnCode.NOT_FOUND,
                Message.Messages.NOT_FOUND
        );

        log.warn(STR."Trace ID: \{traceId} \{resource} not found in \{path}");
    }
}
