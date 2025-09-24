package com.cell.model.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "封装前端分配菜单请求参数")
public class SysRoleMenuDto {

    @Schema(description = "角色 id")
    private Long roleId;

    @Schema(description = "菜单 id 集合")
    private List<Long> menuIdList;

}
