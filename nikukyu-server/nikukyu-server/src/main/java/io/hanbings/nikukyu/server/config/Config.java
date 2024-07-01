package io.hanbings.nikukyu.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "nikukyu.config")
public class Config {
    String api;
    String site;
    String name;
    String mail;
    String privacy;
    String tos;
}