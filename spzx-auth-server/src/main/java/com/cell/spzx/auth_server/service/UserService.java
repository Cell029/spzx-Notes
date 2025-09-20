package com.cell.spzx.auth_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cell.model.entity.system.SysUser;
import com.cell.model.entity.user.UserInfo;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService extends IService<UserInfo> {
    UserInfo getUserInfo(HttpServletRequest request);

}
