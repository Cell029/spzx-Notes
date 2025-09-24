package com.cell.model.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cell.model.entity.base.BaseEntity;
import lombok.Data;

@Data
@TableName("sys_role_menu")
public class SysRoleMenu extends BaseEntity {

    private Long roleId;       // 角色id
    private Long menuId;       // 菜单id

}
