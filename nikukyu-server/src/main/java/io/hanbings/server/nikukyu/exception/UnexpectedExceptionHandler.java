package io.hanbings.server.nikukyu.exception;

import io.hanbings.server.nikukyu.data.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;

@Slf4j
public class UnexpectedExceptionHandler {
    @ResponseBody
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(Exception.class)
    public Message<?> allUnexpectedExceptionHandler(Exception e) {
        log.warn("Unexpected Exception: {}", e.getMessage());

        Arrays.stream(e.getStackTrace()).forEach(line -> log.warn("{}", line));

        return new Message<>(Message.ReturnCode.SERVER_ERROR, Message.Messages.SERVER_ERROR, e.getMessage());
    }
}
