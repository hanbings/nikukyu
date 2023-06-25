package io.hanbings.server.nikukyu;

import io.hanbings.server.nikukyu.config.Config;
import io.hanbings.server.nikukyu.config.OAuthConfig;
import io.hanbings.server.nikukyu.content.Resources;
import io.hanbings.server.nikukyu.service.OAuthProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings("SpellCheckingInspection")
public class NikukyuRunner implements CommandLineRunner {
    final OAuthConfig oauth;
    final Config config;

    @Override
    public void run(String... args) {
        // init service
        OAuthProviderService.init(config, oauth);

        log.info(Resources.LOGO);
        log.info("Hello, Nikukyu!");
    }
}
