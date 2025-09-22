package com.cell.model.dto.h5;

import com.cell.model.dto.h5.base.QueryPageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "查询系统角色请求参数")
public class SysUserQueryDto extends QueryPageDto {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "系统用户使用者姓名")
    private String name;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "系统用户状态（0：停用；1：正常）")
    private Integer status;

}
