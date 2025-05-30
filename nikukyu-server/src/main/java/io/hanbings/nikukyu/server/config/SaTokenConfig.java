package io.hanbings.nikukyu.server.config;

import io.hanbings.nikukyu.server.security.SaTokenLoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaTokenLoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/version",
                        "/",
                        "/api",
                        "/api/v0",
                        "/api/v0/login/oauth/**",
                        "/api/v0/login/email/verify",
                        "/api/v0/login/token"
                );
    }
}