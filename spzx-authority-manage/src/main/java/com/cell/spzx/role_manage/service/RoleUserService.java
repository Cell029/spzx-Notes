package com.cell.spzx.role_manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cell.model.dto.system.SysUserAssoRoleDto;
import com.cell.model.entity.system.SysRole;
import com.cell.model.entity.system.SysRoleUser;

import java.util.List;
import java.util.Map;

public interface RoleUserService extends IService<SysRoleUser> {

    void assignRoles(SysUserAssoRoleDto sysUserAssoRoleDto);

    Map<String, List<SysRole>> getUserRoleData(Integer id);

}
