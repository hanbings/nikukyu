package io.hanbings.server.nikukyu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message<T> {
    int code;
    String message;
    T data;

    public Message(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Message<T> success(T data) {
        return new Message<>(ReturnCode.SUCCESS, "好欸！ ╭(○｀∀´○)╯", data);
    }

    public static <T> Message<T> badRequest(T data) {
        return new Message<>(ReturnCode.BAD_REQUEST, "不要乱来啦！ ⁽⁽(੭ꐦ •̀Д•́ )੭*⁾⁾", data);
    }

    public static <T> Message<T> unauthorized(T data) {
        return new Message<>(ReturnCode.UNAUTHORIZED, "你还没有登录！ Σ(•̀ω•́ﾉ)ﾉ", data);
    }

    public static <T> Message<T> forbidden(T data) {
        return new Message<>(ReturnCode.FORBIDDEN, "你沒有权限！ ฅ(º ﾛ º ฅ)", data);
    }

    public static <T> Message<T> notFound(T data) {
        return new Message<>(ReturnCode.NOT_FOUND, "找不到这个东西！ ( ´◔ ‸◔`)", data);
    }

    public static <T> Message<T> serverError(T data) {
        return new Message<>(ReturnCode.SERVER_ERROR, "服务器出错了！ (◍ ´꒳` ◍)", data);
    }

    public static class Messages {
        public static final Message<?> SUCCESS = success(null);
        public static final Message<?> BAD_REQUEST = badRequest(null);
        public static final Message<?> UNAUTHORIZED = unauthorized(null);
        public static final Message<?> FORBIDDEN = forbidden(null);
        public static final Message<?> NOT_FOUND = notFound(null);
        public static final Message<?> SERVER_ERROR = serverError(null);
    }

    public static class ReturnCode {
        public static final int SUCCESS = 200;
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int SERVER_ERROR = 500;
        public static final int MAIL_EXIST = 20000;
        public static final int MAIL_VERIFY_INVALID = 20001;
    }
}
