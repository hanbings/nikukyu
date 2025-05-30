package io.hanbings.nikukyu.server.exception;

import io.hanbings.nikukyu.server.utils.TimeUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class NikukyuException extends RuntimeException {
    int code;
    String message;
    long timestamp;

    public NikukyuException(int code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = TimeUtils.getMilliUnixTime();
    }
}