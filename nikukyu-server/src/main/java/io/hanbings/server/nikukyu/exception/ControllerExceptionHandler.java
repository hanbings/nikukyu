package io.hanbings.server.nikukyu.exception;

import io.hanbings.server.nikukyu.data.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {
    @ResponseBody
    @ExceptionHandler(ControllerException.class)
    public Message<?> exceptionHandler(ControllerException e) {
        log.info("controller exception: {}", e.getMessage());

        return new Message<>(e.getCode(), e.getMessage(), e.getData());
    }
}
