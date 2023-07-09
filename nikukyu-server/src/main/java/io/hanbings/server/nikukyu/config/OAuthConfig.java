package io.hanbings.server.nikukyu.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
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
            Resource resource = new ClassPathResource("oauth.json");
            InputStream input = resource.getInputStream();
            return new ObjectMapper().readValue(input, OAuthConfig.class);
        }
    }
}
