package com.jiangce.yueting.utils;

public class BeanUtils {
    /**
     * 通过类对象创建新实例并拷贝属性
     * @param source 源对象
     * @param targetClass 目标类类型
     * @return 目标类实例
     */
    public static <T> T copyProperties(Object source, Class<T> targetClass) {
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            org.springframework.beans.BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("属性拷贝失败", e);
        }
    }

    public static void copyProperties(Object source, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(source, target);
    }
}
