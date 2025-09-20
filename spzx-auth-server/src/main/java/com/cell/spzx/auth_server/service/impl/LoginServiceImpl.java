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
import com.cell.spzx.common.utils.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
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
    public LoginVo login(UserLoginDto userLoginDto, HttpServletRequest request) {
        UserInfo userInfo = getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUsername, userLoginDto.getUsername()));
        if (userInfo != null) {
            // 校验用户输入的验证码和图片验证码是否一致
            String redisRandomCode = stringRedisTemplate.opsForValue().get(LoginConstant.RANDOM_CODE_KEY + userLoginDto.getCodeKey());
            if (StrUtil.isNotEmpty(redisRandomCode) && redisRandomCode.equals(userLoginDto.getRandomCode())) {
                // 删除存入 Redis 中的图片验证码
                stringRedisTemplate.delete(LoginConstant.RANDOM_CODE_KEY + userLoginDto.getCodeKey());
                String password = userLoginDto.getPassword();
                if (password.equals(userInfo.getPassword())) {
                    // 记录当前登录用户的 IP
                    String ipAddress = IpUtil.getIpAddress(request);
                    // 更新数据库中的 user_info 表，记录登录 IP
                    userInfo.setLastLoginIp(ipAddress);
                    updateById(userInfo);
                    // 账号密码正确，把用户 id 存入 session
                    HttpSession session = request.getSession();
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
    public LoginVo loginWithPhoneCode(UserLoginDto userLoginDto, HttpServletRequest request) {
        String phone = userLoginDto.getPhone();
        String phoneCode = userLoginDto.getPhoneCode();
        String redisPhoneCode = stringRedisTemplate.opsForValue().get(LoginConstant.LOGIN_PHONE_CODE_KEY + phone);
        if (StrUtil.isNotEmpty(redisPhoneCode)) {
            String[] split = redisPhoneCode.split("_");
            // 用户输入的验证码和存入 Redis 的验证码一致
            if (split.length > 0 && phoneCode.equals(split[0])) {
                // 删除存在 Redis 中的验证码并生成 session
                stringRedisTemplate.delete(LoginConstant.LOGIN_PHONE_CODE_KEY + phone);
                // 只有验证码通过后再通过手机号查询是否存在该用户
                UserInfo userInfo = getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getPhone, userLoginDto.getPhone()));
                log.info("当前用户：" + userInfo);
                if (userInfo != null) {
                    // 记录当前登录用户的 IP
                    String ipAddress = IpUtil.getIpAddress(request);
                    // 更新数据库中的 user_info 表，记录登录 IP
                    userInfo.setLastLoginIp(ipAddress);
                    updateById(userInfo);
                    // 创建 session
                    HttpSession session = request.getSession();
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

    @Override
    public Boolean checkLoginCount(UserLoginDto userLoginDto, HttpServletRequest request) {
        // 根据用户名或手机号查用户信息
        String keyForUser = LoginConstant.LOGIN_LIMIT_KEY + (StrUtil.isEmpty(userLoginDto.getUsername()) ? userLoginDto.getPhone() : userLoginDto.getUsername());
        Boolean resultForUser = checkLoginCount(keyForUser);
        Boolean resultForIp = false;
        String ipAddress = IpUtil.getIpAddress(request);
        if (StrUtil.isNotEmpty(ipAddress)) {
            String keyForUserIp = LoginConstant.LOGIN_LIMIT_IP_KEY + ipAddress;
            resultForIp = checkLoginCount(keyForUserIp);
        }
        return resultForUser && resultForIp;
    }

    public Boolean checkLoginCount(String key) {
        Long loginCount = stringRedisTemplate.opsForValue().increment(key);
        if (loginCount != null && loginCount == 1) {
            stringRedisTemplate.expire(key, LoginConstant.LOGIN_COUNT_SURVIVE_TTL, TimeUnit.SECONDS);
        }
        return loginCount != null && loginCount <= LoginConstant.LOGIN_COUNT_LIMIT;
    }

    @Override
    public void loginOut(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            log.info("用户退出，sessionId：" + session.getId());
            session.invalidate(); // 触发 SpringSession 删除 Redis 中的记录
        }
    }


}
