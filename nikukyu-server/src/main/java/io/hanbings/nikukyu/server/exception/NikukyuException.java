package io.hanbings.nikukyu.server.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hanbings.nikukyu.server.data.Message;
import io.hanbings.nikukyu.server.data.RequestTrace;
import io.hanbings.nikukyu.server.utils.RandomUtils;
import io.hanbings.nikukyu.server.utils.TimeUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;
import java.util.Map;

@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
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
        public ResponseEntity<Map> handleNikukyuException(NikukyuException e) {
            log.warn(STR."\nTrace ID: \{e.traceId}\nReturn Code: \{e.code}\nMessage: \{e.message}\nTime: \{e.timestamp}\nStack Trace: \{Arrays.toString(e.getStackTrace())}\n");

            return new ResponseEntity<>(
                    Map.of(
                            "traceId", e.getTraceId(),
                            "code", e.getCode(),
                            "message", e.getMessage(),
                            "timestamp", e.getTimestamp()
                    ),
                    HttpStatusCode.valueOf(e.getCode())
            );
        }
    }

    @ControllerAdvice
    public static class GlobalExceptionHandler {
        ObjectMapper mapper = new ObjectMapper();

        @SuppressWarnings("all")
        @ExceptionHandler(Exception.class)
        public ResponseEntity<Map> handleMyException(HttpServletRequest request, Exception e) throws JsonProcessingException {
            String traceId = RandomUtils.uuid();
            RequestTrace requestTrace = RequestTrace.parse(traceId, request);

            log.error(STR."\nRequest URL: \{request.getRequestURL()}\nMessage: \{e.getMessage()}\nTime: \{TimeUtils.getMilliUnixTime()}\nRequest Data: \{mapper.writeValueAsString(requestTrace)}\nStack Trace: \{Arrays.toString(e.getStackTrace())}");

            return new ResponseEntity<>(
                    Map.of(
                            "traceId", traceId,
                            "code", Message.ReturnCode.SERVER_ERROR,
                            "message", Message.Messages.SERVER_ERROR,
                            "timestamp", TimeUtils.getMilliUnixTime()
                    ),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }
}
