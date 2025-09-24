package com.cell.model.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "请求参数实体类")
public class AssignMenuDto {

    @Schema(description = "角色id")
    private Long roleId;

    /*@Schema(description = "选中的菜单id的集合")
    private List<Map<String , Number>> menuIdList; // 选中的菜单id的集合; Map的键表示菜单的id，值表示是否为半开; 0否，1是*/

    @Schema(description = "选中的菜单id的集合")
    private List<MenuSelectDto> menuIdList;
}

@Data
class MenuSelectDto {
    private Long menuId;
    private Integer halfChecked; // 0=半选, 1=全选
}