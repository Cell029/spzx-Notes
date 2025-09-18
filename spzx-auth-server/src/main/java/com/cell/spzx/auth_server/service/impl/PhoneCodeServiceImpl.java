package com.cell.spzx.auth_server.service.impl;

import cn.hutool.core.util.StrUtil;
import com.cell.spzx.auth_server.service.PhoneCodeService;
import cn.hutool.core.util.RandomUtil;
import com.cell.spzx.common.constant.PhoneConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service("phoneCodeService")
public class PhoneCodeServiceImpl implements PhoneCodeService {

    // 配置 log
    private static final Logger log = Logger.getLogger(PhoneCodeServiceImpl.class.getName());

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Boolean generatePhoneCode(String phone) {
        // 先从 Redis 中查找是否有该号码的验证码
        String redisPhoneCode = stringRedisTemplate.opsForValue().get(PhoneConstant.LOGIN_PHONE_CODE_KEY + phone);
        if (StrUtil.isNotEmpty(redisPhoneCode)) {
            String[] split = redisPhoneCode.split("_");
            if (split.length == 2) {
                long time = Long.parseLong(split[1]);
                // 当前时间减去生成验证码的时间，如果小于设置的验证码过期时间，那么就不再生成新的验证码
                if (System.currentTimeMillis() - time < TimeUnit.SECONDS.toMillis(PhoneConstant.LOGIN_PHONE_CODE_TTL)) {
                    log.info("还未过期的手机验证码:" + split[0]);
                    return false;
                }
            }
        }
        // 让生成的验证码携带当前时间戳，用来后续判断是否需要继续生成新的验证码
        String phoneCode = RandomUtil.randomNumbers(6);
        String code = phoneCode + "_" + System.currentTimeMillis();
        // 将验证码存入 Redis 中，并设置过期时间为 60 s
        stringRedisTemplate.opsForValue().set(PhoneConstant.LOGIN_PHONE_CODE_KEY + phone, code,
                PhoneConstant.LOGIN_PHONE_CODE_TTL, TimeUnit.SECONDS);
        log.info("新的手机验证码:" + phoneCode);
        return true;
    }
}
