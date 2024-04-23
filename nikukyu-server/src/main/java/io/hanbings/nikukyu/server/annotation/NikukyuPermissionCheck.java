package io.hanbings.nikukyu.server.annotation;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hanbings.nikukyu.server.data.Message;
import io.hanbings.nikukyu.server.exception.UnauthorizationException;
import io.hanbings.nikukyu.server.security.Header;
import io.hanbings.nikukyu.server.security.Permission;
import io.hanbings.nikukyu.server.security.Token;
import io.hanbings.nikukyu.server.service.TokenService;
import io.hanbings.nikukyu.server.utils.RandomUtils;
import io.hanbings.nikukyu.server.utils.TimeUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("SpellCheckingInspection")
public @interface NikukyuPermissionCheck {
    String[] access();

    boolean requiredLogin() default true;

    boolean requiredAllAccess() default true;

    boolean wrapperData() default true;

    @Aspect
    @Component
    @RequiredArgsConstructor
    @SuppressWarnings("all")
    public class AccessChecker {
        static ObjectMapper mapper = new ObjectMapper();
        static JavaType listType = mapper.getTypeFactory().constructParametricType(ArrayList.class, Permission.class);
        final TokenService tokenService;

        @SneakyThrows
        @Around(value = "@annotation(io.hanbings.nikukyu.server.annotation.NikukyuPermissionCheck)")
        public Object check(ProceedingJoinPoint point) {
            // 获取注解所规定的权限
            MethodSignature signature = (MethodSignature) point.getSignature();
            Object target = point.getTarget();
            Method method = target.getClass().getMethod(signature.getName(), signature.getParameterTypes());
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            NikukyuPermissionCheck annotation = method.getAnnotation(NikukyuPermissionCheck.class);

            // 获取请求头的 Auhtoization header
            String authorization = request.getHeader("Authorization");

            // 检查是否需要登录
            // 如果需要登录没有 Token 则返回未授权错误
            if (annotation.requiredLogin() && authorization == null)
                throw new UnauthorizationException(RandomUtils.uuid(), request.getRequestURI());

            // 获取 Token
            Token token = tokenService.parse(authorization);

            // 检查 Token 是否过期
            if (annotation.requiredLogin() && token == null)
                throw new UnauthorizationException(RandomUtils.uuid(), request.getRequestURI());

            // 检查 Token 是否有权限
            // 校验登录后检查是否需要检查全部的权限
            Set<String> willBeCheck = Set.of(annotation.access());

            // 检测权限
            if (!annotation.requiredAllAccess()) {
                if (!tokenService.checkAccess(token, willBeCheck)) {
                    throw new UnauthorizationException(RandomUtils.uuid(), request.getRequestURI());
                }
            } else {
                for (String permission : willBeCheck) {
                    if (!tokenService.checkAccess(token, permission)) {
                        throw new UnauthorizationException(RandomUtils.uuid(), request.getRequestURI());
                    }
                }
            }

            // 设置 Account ID 到请求中
            request.setAttribute(Header.ACCOUNT, token.belong());
            if (token != null) {
                request.setAttribute(Header.Custom_Header, token);
            }

            // 构造消息
            if (annotation.wrapperData()) {
                Message<Object> message = new Message<>(
                        RandomUtils.uuid(),
                        Message.ReturnCode.SUCCESS,
                        Message.Messages.SUCCESS,
                        TimeUtils.getMilliUnixTime(),
                        point.proceed()
                );

                return message;
            } else {
                return point.proceed();
            }
        }

        private Class<?>[] getParameterTypes(ProceedingJoinPoint point) {
            Object[] args = point.getArgs();
            Class<?>[] parameterTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i].getClass();
            }
            return parameterTypes;
        }
    }
}
