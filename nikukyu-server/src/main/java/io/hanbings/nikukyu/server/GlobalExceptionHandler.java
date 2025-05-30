package io.hanbings.nikukyu.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.hanbings.nikukyu.server.exception.LogicException;
import io.hanbings.nikukyu.server.exception.NikukyuException;
import io.hanbings.nikukyu.server.exception.NoAuthException;
import io.hanbings.nikukyu.server.exception.NotFoundException;
import io.hanbings.nikukyu.server.utils.TimeUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "code", 500,
                "error", ex.getMessage(),
                "message", "请求失败"
        ));
    }

    @ResponseBody
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "code", 404,
                "error", ex.getMessage(),
                "message", "请求失败"
        ));
    }

    @ResponseBody
    @ExceptionHandler(NoAuthException.class)
    public ResponseEntity<?> handleException(NoAuthException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "code", 401,
                "error", ex.getMessage(),
                "message", "未登录"
        ));
    }

    @ResponseBody
    @ExceptionHandler(LogicException.class)
    public ResponseEntity<?> handleException(LogicException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "code", 503,
                "error", ex.getMessage(),
                "message", "内部逻辑错误"
        ));
    }

    @SuppressWarnings("all")
    @ExceptionHandler(NikukyuException.class)
    public ResponseEntity<?> handleMyException(HttpServletRequest request, NikukyuException e) throws JsonProcessingException {
        HttpStatus status = HttpStatus.valueOf(e.getCode());
        String message = e.getMessage();
        long time = TimeUtils.getMilliUnixTime();

        log.warn(
                """
                        Request URL: {request.getRequestURL()}
                        Message: {e.getMessage()}
                        Time: {time}
                        Stack Trace: {Arrays.toString(e.getStackTrace())}
                        """,
                request.getRequestURL(),
                e.getMessage(),
                time,
                Arrays.toString(e.getStackTrace())
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "code", status,
                "error", e.getMessage(),
                "message", "内部逻辑错误"
        ));
    }
}