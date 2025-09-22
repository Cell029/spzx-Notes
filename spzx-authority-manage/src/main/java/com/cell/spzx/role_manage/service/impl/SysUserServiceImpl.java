package com.cell.spzx.role_manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cell.model.dto.h5.SysUserQueryDto;
import com.cell.model.entity.system.SysRole;
import com.cell.model.entity.system.SysUser;
import com.cell.model.vo.h5.PageResult;
import com.cell.spzx.role_manage.mapper.SysUserMapper;
import com.cell.spzx.role_manage.service.SysUserService;
import org.springframework.stereotype.Service;

@Service("sysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Override
    public void addSysUser(SysUser sysUser) {
        save(sysUser);
    }

    @Override
    public PageResult<SysUser> selectSysUserPage(SysUserQueryDto sysUserQueryDto) {
        // 构造查询条件
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (sysUserQueryDto.getUsername() != null && !sysUserQueryDto.getUsername().isEmpty()) {
            lambdaQueryWrapper.like(SysUser::getUsername, sysUserQueryDto.getUsername());
        }
        if (sysUserQueryDto.getName() != null && !sysUserQueryDto.getName().isEmpty()) {
            lambdaQueryWrapper.like(SysUser::getName, sysUserQueryDto.getName());
        }
        if (sysUserQueryDto.getPhone() != null && !sysUserQueryDto.getPhone().isEmpty()) {
            lambdaQueryWrapper.eq(SysUser::getPhone, sysUserQueryDto.getPhone());
        }
        // 构造分页对象
        Page<SysUser> page = new Page<>(sysUserQueryDto.getPage(), sysUserQueryDto.getSize());
        // 查询后的结果
        Page<SysUser> sysRolePage = page(page, lambdaQueryWrapper);

        return new PageResult<SysUser>(sysRolePage.getTotal(), sysUserQueryDto.getPage(), sysRolePage.getRecords());
    }
}
