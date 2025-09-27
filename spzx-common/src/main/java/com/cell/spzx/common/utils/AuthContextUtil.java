package com.cell.spzx.common.utils;


public class AuthContextUtil {
    // 创建一个 ThreadLocal 对象，用来存储用户 id
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    // 定义存储数据的静态方法
    public static void set(Long userId) {
        threadLocal.set(userId);
    }

    // 定义获取数据的方法
    public static Long get() {
        return threadLocal.get() ;
    }

    // 删除数据的方法
    public static void remove() {
        threadLocal.remove();
    }
}
