package io.hanbings.server.nikukyu.exception;

import io.hanbings.server.nikukyu.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class AllExceptionHandler {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Message<?> exceptionHandler(Exception e) {
        log.info("exception: {}", e.getMessage());
        return Message.Messages.SERVER_ERROR;
    }
}
