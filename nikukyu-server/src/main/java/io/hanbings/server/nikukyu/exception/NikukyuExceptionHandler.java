package io.hanbings.server.nikukyu.exception;

import io.hanbings.server.nikukyu.data.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class NikukyuExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmailVerifyException.class)
    public Message<?> emailVerifyExceptionHandler(EmailVerifyException e) {
        return new Message<>(e.getCode(), e.getMessage(), null);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public Message<?> notFoundExceptionHandler(NotFoundException e) {
        return new Message<>(e.getCode(), e.getMessage(), null);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(OAuthAuthorizeException.class)
    public Message<?> oAuthAuthorizeExceptionHandler(OAuthAuthorizeException e) {
        return new Message<>(e.getCode(), e.getMessage(), null);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(OAuthProviderException.class)
    public Message<?> oAuthProviderExceptionHandler(OAuthProviderException e) {
        return new Message<>(e.getCode(), e.getMessage(), null);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ParamsException.class)
    public Message<?> paramsExceptionHandler(ParamsException e) {
        return new Message<>(e.getCode(), e.getMessage(), null);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public Message<?> unauthorizedExceptionHandler(UnauthorizedException e) {
        return new Message<>(e.getCode(), e.getMessage(), null);
    }
}
