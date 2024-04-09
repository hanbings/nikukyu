package io.hanbings.nikukyu.server.exception;

import io.hanbings.nikukyu.server.data.Message;
import io.hanbings.nikukyu.server.utils.RandomUtils;
import io.hanbings.nikukyu.server.utils.TimeUtils;

@SuppressWarnings("SpellCheckingInspection")
public class UnauthorizationException extends NikukyuException {
    public UnauthorizationException() {
        super(
                RandomUtils.uuid(),
                Message.ReturnCode.UNAUTHORIZED,
                String.valueOf(Message.ReturnCode.UNAUTHORIZED),
                TimeUtils.getMilliUnixTime()
        );
    }
}
