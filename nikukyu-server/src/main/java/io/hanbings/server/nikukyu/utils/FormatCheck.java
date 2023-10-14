package io.hanbings.server.nikukyu.utils;

public class FormatCheck {
    final static String regex = "\\b[\\w.%-]+@[\\w.-]+\\.[A-Za-z]{2,}\\b";

    public static boolean checkEmail(String email) {
        return email.matches(regex);
    }
}
