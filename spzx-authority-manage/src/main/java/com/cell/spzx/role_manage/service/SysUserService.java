package com.cell.spzx.role_manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cell.model.dto.h5.SysUserQueryDto;
import com.cell.model.entity.system.SysUser;
import com.cell.model.vo.h5.PageResult;

public interface SysUserService extends IService<SysUser> {

    void addSysUser(SysUser sysUser);

    PageResult<SysUser> selectSysUserPage(SysUserQueryDto sysUserQueryDto);

}
