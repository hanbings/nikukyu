package io.hanbings.nikukyu.server.annotation;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hanbings.nikukyu.server.data.Message;
import io.hanbings.nikukyu.server.security.Permission;
import io.hanbings.nikukyu.server.utils.RandomUtils;
import io.hanbings.nikukyu.server.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("SpellCheckingInspection")
public @interface NikukyuPermissionCheck {
    String[] access();

    boolean requiredLogin() default true;

    boolean requiredAllAccess() default true;

    @Aspect
    @Component
    @RequiredArgsConstructor
    @SuppressWarnings("all")
    public class AccessChecker {
        static ObjectMapper mapper = new ObjectMapper();
        static JavaType listType = mapper.getTypeFactory().constructParametricType(ArrayList.class, Permission.class);

        final

        @SneakyThrows
        @Around(value = "@annotation(io.hanbings.nikukyu.server.annotation.NikukyuPermissionCheck)")
        public Message<Object> check(ProceedingJoinPoint point) {
            // 获取注解所规定的权限
            MethodSignature signature = (MethodSignature) point.getSignature();
            Object target = point.getTarget();
            Method method = target.getClass().getMethod(signature.getName(), signature.getParameterTypes());
            NikukyuPermissionCheck annotation = method.getAnnotation(NikukyuPermissionCheck.class);

            // 构造消息
            Message<Object> message = new Message<>(
                    RandomUtils.uuid(),
                    Message.ReturnCode.SUCCESS,
                    Message.Messages.SUCCESS,
                    TimeUtils.getMilliUnixTime(),
                    point.proceed()
            );

            return message;
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
