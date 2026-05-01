package com.jiangce.yueting.utils;

public class EmailUtils {

    /**
     * 获取加密后的邮箱（如 123456789@qq.com → ***789@qq.com）
     *
     * @param email 邮箱地址
     */
    public static String getMaskedEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "";
        }

        // 分割用户名和域名
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return email; // 非法邮箱格式，直接返回原值
        }

        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        // 处理用户名：保留最后 3 位，前面用 * 填充
        if (username.length() <= 3) {
            // 如果用户名不足 3 位，全部替换为 *
            return new String(new char[username.length()]).replace('\0', '*') + domain;
        }

        String maskedUsername =
                new String(new char[username.length() - 3]).replace('\0', '*') +
                        username.substring(username.length() - 3);

        return maskedUsername + domain;
    }
}
