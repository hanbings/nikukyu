package io.hanbings.server.nikukyu.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public record OAuthConfig(Proxy proxy, Map<ProviderType, Provider> providers) {
    public enum ProviderType {
        @JsonProperty("github")
        GITHUB;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    public record Provider(
            @JsonProperty("enable") boolean enable,
            @JsonProperty("proxy") boolean proxy,
            @JsonProperty("client_id") String clientId,
            @JsonProperty("client_secret") String clientSecret
    ) {
    }

    public record Proxy(String host, String port, String username, String password) {
    }

    @Configuration
    static class OAuthConfigBean {
        @Bean
        public OAuthConfig oaths() throws IOException {
            // 读取到配置文件是否存在
            if (!new File("oauth.json").exists()) {
                Resource resource = new ClassPathResource("oauth.json");
                InputStream input = resource.getInputStream();

                // 释放到项目根目录
                File file = new File("oauth.json");
                boolean ignore = file.createNewFile();

                // 使用 Files 写入
                Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

                throw new IOException("oauth.json not found");
            }

            // 读取到配置文件
            InputStream input = Files.newInputStream(new File("oauth.json").toPath());
            return new ObjectMapper().readValue(input, OAuthConfig.class);
        }
    }
}
