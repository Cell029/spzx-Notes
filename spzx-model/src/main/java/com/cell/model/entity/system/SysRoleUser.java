package com.cell.model.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cell.model.entity.base.BaseEntity;
import lombok.Data;

@Data
@TableName("sys_user_role")
public class SysRoleUser extends BaseEntity {

    private Long roleId;       // 角色id
    private Long userId;       // 用户id

}
