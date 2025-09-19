package com.cell.spzx.common.constant;

public class LoginConstant {
    /**
     * 手机验证码相关
     */
    public static final String LOGIN_PHONE_CODE_KEY = "login:phone:code:";
    public static final Integer LOGIN_PHONE_CODE_TTL = 180;
    public static final Integer GENERATE_PHONE_CODE_LIMIT_TTL = 60;

    /**
     * 登录次数校验相关
     */
    public static final String LOGIN_LIMIT_KEY = "login:limit:";
    public static final Long LOGIN_COUNT_LIMIT = 3L; // 规定时间内允许的登录次数限制
    public static final Integer LOGIN_COUNT_SURVIVE_TTL = 60; // 规定时间

    /**
     * 随机验证码相关
     */
    public static final String RANDOM_CODE_KEY = "random:code:";
    public static final Integer RANDOM_CODE_TTL = 180;
}
