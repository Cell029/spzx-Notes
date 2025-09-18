package com.cell.spzx.auth_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cell.model.dto.h5.UserLoginDto;
import com.cell.model.dto.system.LoginDto;
import com.cell.model.entity.user.UserInfo;
import com.cell.model.vo.system.LoginVo;
import com.cell.spzx.auth_server.mapper.LoginMapper;
import com.cell.spzx.auth_server.service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service("loginService")
public class LoginServiceImpl extends ServiceImpl<LoginMapper, UserInfo> implements LoginService {

    @Override
    public LoginVo login(UserLoginDto userLoginDto, HttpSession session) {
        UserInfo userInfo = getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUsername, userLoginDto.getUsername()));
        if (userInfo == null) {
            return null;
        } else {
            String password = userLoginDto.getPassword();
            if (password.equals(userInfo.getPassword())) {
                // 账号密码正确，把用户 id 存入 session
                session.setAttribute("userId", userInfo.getId());
                // 获取 sessionId 作为 token
                String token = session.getId();
                LoginVo loginVo = new LoginVo();
                loginVo.setToken(token);
                return loginVo;
            } else {
                return null;
            }
        }
    }
}
