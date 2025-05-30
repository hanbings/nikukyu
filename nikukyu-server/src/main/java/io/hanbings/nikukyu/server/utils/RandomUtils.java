package io.hanbings.nikukyu.server.utils;

@SuppressWarnings("unused")
public class RandomUtils {
    public static int number() {
        return (int) (Math.random() * 100);
    }

    public static int number(int max) {
        return (int) (Math.random() * max);
    }

    public static int number(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    public static boolean bool() {
        return Math.random() > 0.5;
    }

    public static boolean bool(double chance) {
        return Math.random() < chance;
    }

    public static String string(int length) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < length; i++) {
            str.append((char) number(65, 90));
        }
        return str.toString();
    }

    public static String uuid() {
        return java.util.UUID.randomUUID().toString();
    }
}