package io.hanbings.server.nikukyu.exception;

import io.hanbings.server.nikukyu.data.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class RequestParamsExceptionHandler {
    @ResponseBody
    @ExceptionHandler(RequestParamsException.class)
    public Message<?> exceptionHandler(RequestParamsException e) {
        log.info("exception: {}", e.getMessage());
        return Message.Messages.BAD_REQUEST;
    }
}
