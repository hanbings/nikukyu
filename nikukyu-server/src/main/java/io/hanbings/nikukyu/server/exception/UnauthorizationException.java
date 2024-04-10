package io.hanbings.nikukyu.server.exception;

import io.hanbings.nikukyu.server.data.Message;
import io.hanbings.nikukyu.server.utils.RandomUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("SpellCheckingInspection")
public class UnauthorizationException extends NikukyuException {
    public UnauthorizationException(String path) {
        super(
                RandomUtils.uuid(),
                Message.ReturnCode.UNAUTHORIZED,
                Message.Messages.UNAUTHORIZED
        );

        log.warn("Catch UnauthorizationException: {}", path);
    }
}
