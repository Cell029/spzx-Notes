package com.cell.spzx.auth_server.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cell.model.dto.h5.UserLoginDto;
import com.cell.model.entity.user.UserInfo;
import com.cell.model.vo.system.LoginVo;
import com.cell.spzx.auth_server.mapper.LoginMapper;
import com.cell.spzx.auth_server.service.LoginService;
import com.cell.spzx.common.constant.LoginConstant;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service("loginService")
public class LoginServiceImpl extends ServiceImpl<LoginMapper, UserInfo> implements LoginService {

    // 配置 log
    private static final Logger log = Logger.getLogger(LoginServiceImpl.class.getName());

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public LoginVo login(UserLoginDto userLoginDto, HttpSession session) {
        UserInfo userInfo = getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUsername, userLoginDto.getUsername()));
        if (userInfo != null) {
            // 校验用户输入的验证码和图片验证码是否一致
            String redisRandomCode = stringRedisTemplate.opsForValue().get(LoginConstant.RANDOM_CODE_KEY + userLoginDto.getCodeKey());
            if (StrUtil.isNotEmpty(redisRandomCode) && redisRandomCode.equals(userLoginDto.getRandomCode())) {
                // 删除存入 Redis 中的图片验证码
                stringRedisTemplate.delete(LoginConstant.RANDOM_CODE_KEY + userLoginDto.getCodeKey());
                String password = userLoginDto.getPassword();
                if (password.equals(userInfo.getPassword())) {
                    // 账号密码正确，把用户 id 存入 session
                    session.setAttribute("userId", userInfo.getId());
                    // 获取 sessionId 作为 token
                    String token = session.getId();
                    LoginVo loginVo = new LoginVo();
                    loginVo.setToken(token);
                    return loginVo;
                }
            }
        }
        return null;
    }

    @Override
    public LoginVo loginWithPhoneCode(UserLoginDto userLoginDto, HttpSession session) {
        String phone = userLoginDto.getPhone();
        String phoneCode = userLoginDto.getPhoneCode();
        // 通过手机号查询是否存在该用户
        UserInfo userInfo = getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getPhone, userLoginDto.getPhone()));
        log.info("当前用户：" + userInfo);
        // 只有该用户存在时（即完成了注册）再进行验证码的检验
        if (userInfo != null) {
            String redisPhoneCode = stringRedisTemplate.opsForValue().get(LoginConstant.LOGIN_PHONE_CODE_KEY + phone);
            if (StrUtil.isNotEmpty(redisPhoneCode)) {
                String[] split = redisPhoneCode.split("_");
                // 用户输入的验证码和存入 Redis 的验证码一致
                if (phoneCode.equals(split[0])) {
                    // 删除存在 Redis 中的验证码并生成 session
                    stringRedisTemplate.delete(LoginConstant.LOGIN_PHONE_CODE_KEY + phone);
                    session.setAttribute("userId", userInfo.getId());
                    String token = session.getId();
                    LoginVo loginVo = new LoginVo();
                    loginVo.setToken(token);
                    return loginVo;
                }
            }
        }
        return null;
    }

    // TODO 实现对同一 ip 的限制
    @Override
    public Boolean checkLoginCount(UserLoginDto userLoginDto) {
        // 根据用户名或手机号查用户信息
        UserInfo userInfo = getOne(new LambdaQueryWrapper<UserInfo>()
                // 当用户名不为空时作为查询条件
                .eq(StrUtil.isNotEmpty(userLoginDto.getUsername()), UserInfo::getUsername, userLoginDto.getUsername())
                // 当手机号不为空时作为查询条件
                .or(StrUtil.isNotEmpty(userLoginDto.getPhone()),
                        wrapper -> wrapper.eq(UserInfo::getPhone, userLoginDto.getPhone())));
        if (userInfo != null) {
            Long userId = userInfo.getId();
            if (userId != null) {
                String key = LoginConstant.LOGIN_LIMIT_KEY + userId;
                // 第一次 incr 时设置过期时间
                Long loginCount = stringRedisTemplate.opsForValue().increment(key);
                if (loginCount != null && loginCount == 1L) {
                    // 第一次访问，设置 TTL
                    stringRedisTemplate.expire(key, LoginConstant.LOGIN_COUNT_SURVIVE_TTL, TimeUnit.SECONDS);
                }
                // 小于限制次数，允许继续尝试登录
                return loginCount != null && loginCount <= LoginConstant.LOGIN_COUNT_LIMIT;
            }
        }
        return false;
    }


}
