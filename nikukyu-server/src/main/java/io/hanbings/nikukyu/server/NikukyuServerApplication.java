package io.hanbings.nikukyu.server;

import io.hanbings.nikukyu.server.config.DebugConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@SpringBootApplication
@SuppressWarnings("SpellCheckingInspection")
public class NikukyuServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(NikukyuServerApplication.class, args);
    }

    @Data
    @Component
    @AllArgsConstructor
    public static class ApplicationCheck implements ApplicationListener<ApplicationStartedEvent> {
        DebugConfig debugConfig;

        @Override
        public void onApplicationEvent(@NotNull ApplicationStartedEvent event) {
            log.info("""
                    \s
                     _______  .__ __          __                      \s
                     \\      \\ |__|  | ____ __|  | _____.__.__ __    \s
                     /   |   \\|  |  |/ /  |  \\  |/ <   |  |  |      \\
                    /    |    \\  |    <|  |  /    < \\___  |  |  /
                    \\____|__  /__|__|_ \\____/|__|_ \\/ ____|____/   \s
                            \\/        \\/          \\/\\/            \s
                    Nikukyu Server
                    """);

            if (debugConfig.debug()) {
                log.warn("Debug mode is on");
                log.warn("You are running the application in debug mode, which may inject some dangerous options!!!");

                if (!debugConfig.superTokens().isEmpty()) {
                    log.warn("WARNING!!! You are using super tokens in debug mode");
                    log.warn("DEBUG SUPER TOKEN AMOUNT: {}", debugConfig.superTokens().size());
                }

                log.warn("Please do not use this mode in production environment");
            }
        }
    }
}
