package io.hanbings.nikukyu.server.exception;

import io.hanbings.nikukyu.server.data.Message;
import io.hanbings.nikukyu.server.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;

@Slf4j
@SuppressWarnings("SpellCheckingInspection")
public class NikukyuException extends RuntimeException {
    String traceId;
    int code;
    String message;
    long timestamp;

    public NikukyuException(String traceId, int code, String message) {
        this.traceId = traceId;
        this.code = code;
        this.message = message;
        this.timestamp = TimeUtils.getMilliUnixTime();
    }

    @ControllerAdvice
    public static class NikukyuExceptionHandler {
        @SuppressWarnings("all")
        @ExceptionHandler(value = {NikukyuException.class})
        public Message<?> handleException(NikukyuException e) {
            log.warn(STR."\nTrace ID: \{e.traceId}\nReturn Code: \{e.code}\nMessage: \{e.message}\nTime: \{e.timestamp}\nStack Trace: \{Arrays.toString(e.getStackTrace())}\n");

            return new Message<>(
                    e.traceId,
                    e.code,
                    e.message,
                    e.timestamp,
                    null
            );
        }
    }
}
