package io.hanbings.server.nikukyu.service;

import org.springframework.stereotype.Service;

@Service
public class MailService {
    public void sendVerifyMail(String to, String code) {

    }

    public void sendSuccessCreateAccountMail(String to) {

    }

    public void sendSuccessAuthorizeOAuthMail(
            String to,
            String applicationName, String applicationUrl, String applicationAvatar,
            long time, String ip, String location
    ) {

    }
}
