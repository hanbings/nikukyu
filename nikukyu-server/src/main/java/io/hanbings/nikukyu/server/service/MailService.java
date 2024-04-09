package io.hanbings.nikukyu.server.service;

import io.hanbings.nikukyu.server.config.Config;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class MailService {
    static String VERIFY_MAIL_CONTENT;
    final Config config;
    final JavaMailSender sender;

    public MailService(JavaMailSender sender, Config config) throws IOException {
        this.sender = sender;
        this.config = config;

        Resource resource = new ClassPathResource("verify.email.html");
        InputStream input = resource.getInputStream();

        MailService.VERIFY_MAIL_CONTENT = new String(input.readAllBytes());
    }

    public void sendVerifyMail(String to, String code) throws MessagingException {
        // {{application.verify.code}}
        // {{application.url}}
        // {{application.privacy.policy}}
        // {{application.terms.of.use}}
        // {{application.name}}

        String content = MailService.VERIFY_MAIL_CONTENT
                .replace("{{application.verify.code}}", code)
                .replace("{{application.url}}", config.getSite() + "/login/email/verify")
                .replace("{{application.privacy.policy}}", config.getPrivacy())
                .replace("{{application.terms.of.use}}", config.getTos())
                .replace("{{application.name}}", config.getName());

        // 发送邮件
        //获取MimeMessage对象
        MimeMessage message = sender.createMimeMessage();
        message.setSubject(String.format("【%s 验证邮件】 验证码 %s", config.getName(), code));

        MimeMessageHelper messageHelper;
        messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setFrom(config.getMail());
        messageHelper.setTo(InternetAddress.parse(to));
        messageHelper.setText(content, true);

        //发送
        sender.send(message);
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