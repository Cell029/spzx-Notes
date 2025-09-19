package com.cell.spzx.auth_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cell.model.dto.h5.UserLoginDto;
import com.cell.model.dto.system.LoginDto;
import com.cell.model.entity.user.UserInfo;
import com.cell.model.vo.system.LoginVo;
import jakarta.servlet.http.HttpSession;

public interface LoginService extends IService<UserInfo> {

    LoginVo login(UserLoginDto userLoginDto, HttpSession session);

    LoginVo loginWithPhoneCode(UserLoginDto userLoginDto, HttpSession session);

    Boolean checkLoginCount(UserLoginDto userLoginDto);
}
