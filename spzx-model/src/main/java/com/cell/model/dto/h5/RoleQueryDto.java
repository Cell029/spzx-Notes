package com.cell.model.dto.h5;

import com.cell.model.dto.h5.base.QueryPageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "查询系统角色请求参数")
public class RoleQueryDto extends QueryPageDto {

    @Schema(description = "角色名")
    private String roleName;

    @Schema(description = "角色编码")
    private String roleCode;

}
