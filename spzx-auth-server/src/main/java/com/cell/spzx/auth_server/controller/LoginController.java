package com.cell.spzx.auth_server.controller;

import com.cell.model.dto.h5.UserLoginDto;
import com.cell.model.vo.common.Result;
import com.cell.model.vo.common.ResultCodeEnum;
import com.cell.model.vo.system.LoginVo;
import com.cell.model.vo.system.ValidateCodeVo;
import com.cell.spzx.auth_server.service.LoginService;
import com.cell.spzx.auth_server.service.PhoneCodeService;
import com.cell.spzx.auth_server.service.RandomCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth-server/login")
@Tag(name = "LoginController", description = "登录相关接口")
public class LoginController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private PhoneCodeService phoneCodeService;
    @Autowired
    private RandomCodeService randomCodeService;


    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用用户名和密码登录系统")
    public Result<LoginVo> login(@RequestBody UserLoginDto userLoginDto, HttpServletRequest request) {
        Boolean loginCondition = loginService.checkLoginCount(userLoginDto, request);
        if (loginCondition) {
            LoginVo loginVo = loginService.login(userLoginDto, request);
            if (loginVo != null) {
                return Result.build(loginVo, ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage());
            } else {
                return Result.build(null, ResultCodeEnum.LOGIN_ERROR.getCode(), ResultCodeEnum.LOGIN_ERROR.getMessage());
            }
        } else {
            return Result.build(null, ResultCodeEnum.LOGIN_TOO_FREQUENTLY.getCode(), ResultCodeEnum.LOGIN_TOO_FREQUENTLY.getMessage());
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

    @PostMapping("/loginWithPhoneCode")
    @Operation(summary = "用户手机验证码登录", description = "使用户手机号和手机验证码登录系统")
    public Result<LoginVo> loginWithCode(@RequestBody UserLoginDto userLoginDto, HttpServletRequest request) {
        Boolean loginCondition = loginService.checkLoginCount(userLoginDto, request);
        // 规定时间内登录次数小于限制，可以登录
        if (loginCondition) {
            // 进行登录请求
            LoginVo loginVo = loginService.loginWithPhoneCode(userLoginDto, request);
            if (loginVo != null) {
                return Result.build(loginVo, ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage());
            } else {
                return Result.build(null, ResultCodeEnum.VALIDATE_CODE_ERROR.getCode(), ResultCodeEnum.VALIDATE_CODE_ERROR.getMessage());
            }
        } else { // 规定时间内登录次数小大于限制，拒绝登录，返回登录过于频繁的提示
            return Result.build(null, ResultCodeEnum.LOGIN_TOO_FREQUENTLY.getCode(), ResultCodeEnum.LOGIN_TOO_FREQUENTLY.getMessage());
        }
    }

    @GetMapping("/generateRandomCode")
    @Operation(summary = "生成登录页面的随机图片验证码", description = "进入登录页面时会发送一个请求，该请求让后端生成一个随机的图片验证码")
    public Result<ValidateCodeVo> generateRandomCode(@RequestParam String randomCodeKey) {
        ValidateCodeVo validateCodeVo = randomCodeService.generateRandomCode(randomCodeKey);
        if (validateCodeVo != null) {
            return Result.build(validateCodeVo, ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage());
        } else {
            return Result.build(null, ResultCodeEnum.DATA_ERROR.getCode(), ResultCodeEnum.DATA_ERROR.getMessage());
        }
    }


    @GetMapping("/loginOut")
    @Operation(summary = "用户退出登录", description = "删除用户登录时生成的 session")
    public Result loginOut(HttpServletRequest request) {
        loginService.loginOut(request);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("/ip")
    @Operation(summary = "获取用户 ip")
    public String getCallerIp(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return ip;
    }

}
