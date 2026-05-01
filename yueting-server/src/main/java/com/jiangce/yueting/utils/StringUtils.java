package com.jiangce.yueting.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isHasLength(String str) {
        return !isEmpty(str);
    }

    public static boolean isAllEmpty(String... s) {
        for (String str : s) {
            if (isHasLength(str)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllHasLength(String... s) {
        for (String str : s) {
            if (isEmpty(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 生成一个指定长度的随机字符串
     *
     * @param length
     * @return
     */
    public static String generateRandomNickname(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 把list的内容按逗号分隔转为字符串
     */
    public static <T> String listToString(List<T> list) {
        return list.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    /**
     * 把字符串转为list<Long>
     */
    public static List<Long> stringToLongList(String str) {
        if (str == null || str.isEmpty()) {
            return new ArrayList<>();
        }
        String[] split = str.split(",");
        return Arrays.stream(split)
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    /**
     * 把字符串转为list<Integer>
     */
    public static List<Integer> stringToIntegerList(String str) {
        if (str == null || str.isEmpty()) {
            return new ArrayList<>();
        }
        String[] split = str.split(",");
        return Arrays.stream(split)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
