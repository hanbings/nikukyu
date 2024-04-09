package io.hanbings.nikukyu.server.security;

public class Log {
    // Account Log Types
    public static final String ACCOUNT_LOGIN = "account login";
    public static final String ACCOUNT_LOGOUT = "account logout";
    public static final String ACCOUNT_CREATE = "account create";
    public static final String ACCOUNT_UPDATE = "account update";
    public static final String ACCOUNT_DELETE = "account delete";

    // OAuth Log Types
    public static final String OAUTH_CREATE = "oauth create";
    public static final String OAUTH_UPDATE = "oauth update";
    public static final String OAUTH_DELETE = "oauth delete";
    public static final String OAUTH_CREATE_OAUTH_CLIENT = "oauth create oauth client";
    public static final String OAUTH_UPDATE_OAUTH_CLIENT = "oauth update oauth client";
    public static final String OAUTH_DELETE_OAUTH_CLIENT = "oauth delete oauth client";
}
