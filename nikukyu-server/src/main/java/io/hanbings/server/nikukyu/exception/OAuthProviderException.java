package io.hanbings.server.nikukyu.exception;

import io.hanbings.server.nikukyu.data.Message;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OAuthProviderException extends RuntimeException {
    final int code;
    final String message;

    public OAuthProviderException() {
        this.code = Message.ReturnCode.OAUTH_PROVIDER_INVALID;
        this.message = Message.Messages.OAUTH_PROVIDER_INVALID;
    }
}
