package com.cell.spzx.auth_server.controller;

import com.cell.model.entity.system.SysUser;
import com.cell.model.entity.user.UserInfo;
import com.cell.model.vo.common.Result;
import com.cell.model.vo.common.ResultCodeEnum;
import com.cell.spzx.auth_server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth-server/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/userInfo")
    @Operation(summary = "获取用户信息", description = "通过 session 拿到用户 id 再查询数据库")
    public Result<UserInfo> getUserInfo(HttpServletRequest request) {
        UserInfo userInfo = userService.getUserInfo(request);
        if (userInfo != null) {
            return Result.build(userInfo, ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage());
        } else {
            return Result.build(null, ResultCodeEnum.LOGIN_AUTH.getCode(), ResultCodeEnum.LOGIN_AUTH.getMessage());
        }
    }

}
