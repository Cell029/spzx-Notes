package com.cell.spzx.role_manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cell.model.dto.h5.RoleQueryDto;
import com.cell.model.entity.system.SysRole;
import com.cell.model.vo.h5.PageResult;

import java.util.List;

public interface RoleManageService extends IService<SysRole> {

    void addSysRole(SysRole sysRole);

    PageResult<SysRole> getSysRoleByPage(RoleQueryDto roleQueryDto);

    // PageResult<SysRole> getSysRoleByPageHelper(RoleQueryDto roleQueryDto);

    void deleteSysRole(List<Long> ids);

    void deleteSysRoleByMB(List<Long> ids);

    void updateSysRoleByMB(SysRole sysRole);

    void updateSysRole(SysRole sysRole);

    SysRole selectById(Long id);

}
