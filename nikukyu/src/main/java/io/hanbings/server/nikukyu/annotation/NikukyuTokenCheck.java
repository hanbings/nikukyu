package io.hanbings.server.nikukyu.annotation;

import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.data.Message;
import io.hanbings.server.nikukyu.exception.RequestUnauthorizedException;
import io.hanbings.server.nikukyu.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.UUID;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("SpellCheckingInspection")
public @interface NikukyuTokenCheck {
    // 检查 Token 所需的权限
    AccessType[] access();

    boolean checkAccount() default true;

    @Aspect
    @Component
    @RequiredArgsConstructor
    class NikukyuTokenChecker {
        final TokenService tokens;

        @SuppressWarnings("DuplicatedCode")
        @Around("@annotation(io.hanbings.server.nikukyu.annotation.NikukyuTokenCheck)  && args(auid)")
        public Message<?> check(ProceedingJoinPoint point, @PathVariable String auid) throws Throwable {
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
            if (request == null) throw new RequestUnauthorizedException("Request Not Have Token");

            // 获取 Header 中的 Token
            String header = request.getHeader("Authorization");
            if (header == null) throw new RequestUnauthorizedException("Request Not Have Token");

            // 裁取 Token
            String token = header.substring(header.indexOf("Bearer ") + 7);
            if (token.isEmpty()) throw new RequestUnauthorizedException("Request Not Have Token");

            // 获取注解所规定的权限
            NikukyuTokenCheck annotation = point
                    .getTarget()
                    .getClass()
                    .getMethod(point.getSignature().getName(), getParameterTypes(point))
                    .getAnnotation(NikukyuTokenCheck.class);

            AccessType[] access = annotation.access();

            // 检查 Token 权限
            if (!tokens.check(token, access)) throw new RequestUnauthorizedException("Token Not Have Access");

            // 检查 Token 有效期
            if (tokens.get(token).expire() < System.currentTimeMillis())
                throw new RequestUnauthorizedException("Token Expired");

            // 检查 Token 所属用户
            if (annotation.checkAccount()) {
                if (!tokens.get(token).belong().equals(UUID.fromString(auid)))
                    throw new RequestUnauthorizedException("Token Not Belong To This Account");
            }

            return (Message<?>) point.proceed();
        }

        Class<?>[] getParameterTypes(ProceedingJoinPoint point) {
            Object[] args = point.getArgs();
            Class<?>[] parameterTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i].getClass();
            }
            return parameterTypes;
        }
    }
}
