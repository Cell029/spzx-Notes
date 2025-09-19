package com.cell.spzx.auth_server.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.cell.model.vo.system.ValidateCodeVo;
import com.cell.spzx.auth_server.service.RandomCodeService;
import com.cell.spzx.common.constant.LoginConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service("randomCodeService")
public class RandomCodeServiceImpl implements RandomCodeService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ValidateCodeVo generateRandomCode(String randomCodeKey) {
        if (randomCodeKey != null) {
            stringRedisTemplate.delete(LoginConstant.RANDOM_CODE_KEY +  randomCodeKey);
        }
        // 生成唯一标识 UUID，用来充当存入 Redis 的 key
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String key = LoginConstant.RANDOM_CODE_KEY + uuid;
        // 生成验证码图片
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(150, 48, 4, 2);
        String randomCode = captcha.getCode();// 4 位验证码的值
        String imageBase64 = captcha.getImageBase64(); // 返回图片验证码，base64 编码方式
        stringRedisTemplate.opsForValue().set(key, randomCode, LoginConstant.RANDOM_CODE_TTL, TimeUnit.SECONDS);
        ValidateCodeVo validateCodeVo = new ValidateCodeVo();
        validateCodeVo.setCodeKey(uuid);
        validateCodeVo.setCodeValue("data:image/png;base64," + imageBase64);
        return  validateCodeVo;
    }
}
