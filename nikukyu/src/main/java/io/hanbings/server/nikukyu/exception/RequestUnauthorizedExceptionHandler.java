package io.hanbings.server.nikukyu.exception;

import io.hanbings.server.nikukyu.data.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class RequestUnauthorizedExceptionHandler {
    @ResponseBody
    @ExceptionHandler(RequestUnauthorizedException.class)
    public Message<?> exceptionHandler(RequestUnauthorizedException e) {
        log.info("exception: {}", e.getMessage());
        return Message.Messages.UNAUTHORIZED;
    }
}
