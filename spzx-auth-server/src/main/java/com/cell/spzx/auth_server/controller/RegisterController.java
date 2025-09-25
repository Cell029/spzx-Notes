package com.cell.spzx.auth_server.controller;

import com.cell.model.dto.h5.UserRegisterDto;
import com.cell.model.vo.common.Result;
import com.cell.spzx.auth_server.service.RegisterService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth-server/register")
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @PutMapping("/register")
    @Operation(summary = "用户注册", description = "输入信息执行注册")
    public Result<Boolean> register(@RequestBody UserRegisterDto UserRegisterDto) {
        return registerService.register(UserRegisterDto);
    }
}
