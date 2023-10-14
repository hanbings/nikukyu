package io.hanbings.server.nikukyu.utils;

import java.util.UUID;
import java.util.stream.IntStream;

public class RandomUtils {
    // 字母表
    private static final String[] TABLE = new String[]{
            "a", "b", "c", "d", "e",
            "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o",
            "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z",
            "0", "1", "2", "3", "4",
            "5", "6", "7", "8", "9"
    };

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

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    // 生成随机字符串
    public static String strings(int length) {
        StringBuilder sb = new StringBuilder();
        IntStream.range(0, length).forEach(i -> sb.append(TABLE[number(TABLE.length)]));
        return sb.toString();
    }
}
