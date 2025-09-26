package com.cell.spzx.auth_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cell.model.entity.system.SysUser;
import com.cell.model.entity.user.UserInfo;
import com.cell.spzx.auth_server.mapper.UserMapper;
import com.cell.spzx.auth_server.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, UserInfo> implements UserService {
    @Override
    public UserInfo getUserInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long userId = (Long) session.getAttribute("userId");
            if (userId != null) {
                return getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getId, userId));
            }
        }
        return null;
    }

    @Override
    public UserInfo getUserInfoById(Long id) {
        return getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getId, id));
    }
}
