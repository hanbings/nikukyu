/*
 * Copyright 2024 FurryVerse
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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