package io.hanbings.nikukyu.server.exception;

import io.hanbings.nikukyu.server.data.Message;
import io.hanbings.nikukyu.server.utils.RandomUtils;
import io.hanbings.nikukyu.server.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;

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

    @SuppressWarnings("all")
    @ExceptionHandler(value = NikukyuException.class)
    public Message<?> handleException(NikukyuException e) {
        log.warn(STR."Trace ID: \{traceId}\n Return Code: \{code}\n Message: \{message}\n Time: \{timestamp}\n \{e.getStackTrace()}\n");

        return new Message<>(
                e.traceId,
                e.code,
                e.message,
                e.timestamp,
                null
        );
    }

    @SuppressWarnings("all")
    @ExceptionHandler(value = Exception.class)
    public Message<?> handleException(Exception e) {
        log.error(STR."Trace ID: \{traceId}\n Return Code: \{code}\n Message: \{message}\n Time: \{timestamp}\n \{e.getStackTrace()}\n");

        return new Message<>(
                RandomUtils.uuid(),
                Message.ReturnCode.SERVER_ERROR,
                Message.Messages.SERVER_ERROR,
                TimeUtils.getMilliUnixTime(),
                null
        );
    }
}
