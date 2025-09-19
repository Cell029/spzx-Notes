package com.cell.model.dto.h5;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户登录请求参数")
public class UserLoginDto {

    @Schema(description = "用户名")
    private String username ;

    @Schema(description = "密码")
    private String password ;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "手机号验证码")
    private String phoneCode;

    @Schema(description = "用户输入的随机验证码")
    private String randomCode;

    @Schema(description = "生成随机验证码时存入 Redis 中的 key")
    private String codeKey;
}