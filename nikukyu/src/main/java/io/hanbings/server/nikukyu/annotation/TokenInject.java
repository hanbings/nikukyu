package io.hanbings.server.nikukyu.annotation;

import io.hanbings.server.nikukyu.data.Message;
import io.hanbings.server.nikukyu.exception.ControllerException;
import io.hanbings.server.nikukyu.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface TokenInject {
    @Aspect
    @Component
    @RequiredArgsConstructor
    class TokenInjector {
        final TokenService tokens;

        @SuppressWarnings("DuplicatedCode")
        @Around("@annotation(io.hanbings.server.nikukyu.annotation.TokenInject)")
        public Message<?> check(ProceedingJoinPoint point) throws Throwable {
            // 获取 HttpServletRequest 对象
            Object[] args = point.getArgs();
            HttpServletRequest request = null;
            for (Object arg : args) {
                if (arg instanceof HttpServletRequest) {
                    request = (HttpServletRequest) arg;
                    break;
                }
            }

            // 检查 Token
            if (request == null) throw new ControllerException(Message.Messages.UNAUTHORIZED);

            // 获取 Header 中的 Token
            String header = request.getHeader("Authorization");
            if (header == null) throw new ControllerException(Message.Messages.UNAUTHORIZED);

            // 裁取 Token
            String token = header.substring(header.indexOf("Bearer ") + 7);
            if (token.isEmpty()) throw new ControllerException(Message.Messages.UNAUTHORIZED);

            // 注入
            point.getArgs()[0] = tokens.get(token);

            return (Message<?>) point.proceed();
        }
    }
}
