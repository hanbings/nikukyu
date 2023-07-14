package io.hanbings.server.nikukyu.exception;

import io.hanbings.server.nikukyu.data.Message;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OAuthAuthorizeException extends RuntimeException {
    final int code;
    final String message;

    public OAuthAuthorizeException() {
        this.code = Message.ReturnCode.OAUTH_AUTHORIZE_INVALID;
        this.message = Message.Messages.OAUTH_AUTHORIZE_INVALID;
    }
}
