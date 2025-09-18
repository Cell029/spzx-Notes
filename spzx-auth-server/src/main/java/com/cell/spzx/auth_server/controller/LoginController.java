package com.cell.spzx.auth_server.controller;

import com.cell.model.dto.h5.UserLoginDto;
import com.cell.model.dto.system.LoginDto;
import com.cell.model.vo.common.Result;
import com.cell.model.vo.common.ResultCodeEnum;
import com.cell.model.vo.system.LoginVo;
import com.cell.spzx.auth_server.service.LoginService;
import com.cell.spzx.auth_server.service.PhoneCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController("/auth-server")
@Tag(name = "LoginController", description = "登录相关接口")
public class LoginController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private PhoneCodeService phoneCodeService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用用户名和密码登录系统")
    public Result<LoginVo> login(@RequestBody UserLoginDto userLoginDto, HttpSession session) {
        LoginVo loginVo = loginService.login(userLoginDto, session);
        if (loginVo != null) {
            return Result.build(loginVo, ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage());
        } else {
            return Result.build(null, ResultCodeEnum.LOGIN_ERROR.getCode(), ResultCodeEnum.LOGIN_ERROR.getMessage());
        }
    }

    @GetMapping("/generatePhoneCode")
    @Operation(summary = "生成手机验证码", description = "前端发送请求可以获取到随机手机验证码")
    public Result<String> generatePhoneCode(@RequestParam String phone) {
        Boolean result = phoneCodeService.generatePhoneCode(phone);
        if (result) {
            return Result.build("success", ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage());
        } else {
            return Result.build("fail", ResultCodeEnum.PHONE_CODE_IS_EXISTS.getCode(), ResultCodeEnum.PHONE_CODE_IS_EXISTS.getMessage());
        }
    }

    /*@PostMapping("/loginWithPhoneCode")
    @Operation(summary = "用户登录", description = "使用户手机号和手机验证码登录系统")
    public Result<LoginVo> loginWithCode(@RequestBody , HttpSession session) {
        loginService.loginWithPhoneCode()
    }*/

}
