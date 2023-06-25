package io.hanbings.server.nikukyu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@SuppressWarnings("SpellCheckingInspection")
@ConfigurationProperties(prefix = "nikukyu.config")
public class Config {
    String api;
    String site;
}
