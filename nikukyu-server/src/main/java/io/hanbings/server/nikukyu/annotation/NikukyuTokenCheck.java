package io.hanbings.server.nikukyu.annotation;

import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.data.Message;
import io.hanbings.server.nikukyu.exception.ControllerException;
import io.hanbings.server.nikukyu.model.Account;
import io.hanbings.server.nikukyu.model.OAuth;
import io.hanbings.server.nikukyu.service.AccountService;
import io.hanbings.server.nikukyu.service.OAuthService;
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
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("SpellCheckingInspection")
public @interface NikukyuTokenCheck {
    // 检查 Token 所需的权限
    AccessType[] access();

    // 检查 Token 所需的账户 AUID 从 PathVariable 中获取
    boolean checkAccount() default false;

    // 检查 OAuth 的请求中所属的账户 OUID 从 RequestBody 中获取
    boolean checkOAuth() default false;

    // 符合其中一个权限即可通过检查
    boolean checkAccessOr() default false;

    @Aspect
    @Component
    @RequiredArgsConstructor
    class NikukyuTokenChecker {
        final TokenService tokens;
        final OAuthService oAuthService;
        final AccountService accountService;

        @SuppressWarnings("DuplicatedCode")
        @Around(
                value = "@annotation(io.hanbings.server.nikukyu.annotation.NikukyuTokenCheck) && args(auid, ouid)",
                argNames = "point,auid,ouid"
        )
        public Message<?> check(
                ProceedingJoinPoint point,
                @PathVariable(required = false) String auid,
                @PathVariable(required = false) String ouid
        ) throws Throwable {
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

            // 获取注解所规定的权限
            NikukyuTokenCheck annotation = point
                    .getTarget()
                    .getClass()
                    .getMethod(point.getSignature().getName(), getParameterTypes(point))
                    .getAnnotation(NikukyuTokenCheck.class);

            AccessType[] access = annotation.access();

            // 检查 Token 权限
            if (annotation.checkAccessOr()) {
                boolean target = Arrays
                        .stream(access)
                        .anyMatch(accessType -> tokens.checkAccess(token, new AccessType[]{accessType}));

                if (!target) throw new ControllerException(Message.Messages.UNAUTHORIZED);
            } else {
                if (!tokens.checkAccess(token, access)) {
                    throw new ControllerException(Message.Messages.UNAUTHORIZED);
                }
            }

            // 检查 Token 有效期
            if (tokens.get(token).expire() < System.currentTimeMillis()) {
                throw new ControllerException(Message.Messages.UNAUTHORIZED);
            }

            // 检查 Token 所属用户
            if (annotation.checkAccount()) {
                if (!tokens.get(token).belong().equals(UUID.fromString(auid))) {
                    throw new ControllerException(Message.Messages.UNAUTHORIZED);
                }
            }

            // 检查 OAuth 的所属用户
            if (annotation.checkOAuth()) {
                Account account = accountService.getAccountWithAuid(UUID.fromString(auid));
                OAuth oAuth = oAuthService.getOAuthWithOuid(UUID.fromString(ouid));

                if (Objects.equals(account.auid(), oAuth.auid())) {
                    throw new ControllerException(Message.Messages.UNAUTHORIZED);
                }
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