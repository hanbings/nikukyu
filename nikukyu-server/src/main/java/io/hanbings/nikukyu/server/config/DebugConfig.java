package io.hanbings.nikukyu.server.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hanbings.nikukyu.server.model.Account;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public record DebugConfig(
        @JsonProperty("debug") boolean debug,
        @JsonProperty("super_tokens") List<SuperToken> superTokens
) {
    public record SuperToken(
            @JsonProperty("token") String token,
            @JsonProperty("expire") long expire,
            @JsonProperty("account") Account account
    ) {
    }

    @Configuration
    static class DebugConfigBean {

        @Bean
        public DebugConfig debugConfig() throws IOException {
            // 读取到配置文件是否存在
            if (!new File("debug.json").exists()) {
                Resource resource = new ClassPathResource("debug.json");
                InputStream input = resource.getInputStream();

                // 释放到项目根目录
                File file = new File("debug.json");
                boolean ignore = file.createNewFile();

                // 使用 Files 写入
                Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

                throw new IOException("debug.json not found");
            }

            // 读取到配置文件
            InputStream input = Files.newInputStream(new File("debug.json").toPath());
            return new ObjectMapper().readValue(input, DebugConfig.class);
        }
    }
}
