package com.cell.spzx.role_manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cell.model.dto.h5.RoleQueryDto;
import com.cell.model.entity.system.SysRole;
import com.cell.model.vo.h5.PageResult;
import com.cell.spzx.role_manage.mapper.RoleManageMapper;
import com.cell.spzx.role_manage.service.RoleManageService;
/*import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("roleManageService")
public class RoleManageServiceImpl extends ServiceImpl<RoleManageMapper, SysRole> implements RoleManageService {

    @Autowired
    private RoleManageMapper roleManageMapper;

    @Override
    public void addSysRole(SysRole sysRole) {
        save(sysRole);
    }

    @Override
    public PageResult<SysRole> getSysRoleByPage(RoleQueryDto roleQueryDto) {
        LambdaQueryWrapper<SysRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 多个条件默认就是 AND 关系
        if (roleQueryDto.getRoleName() != null) {
            lambdaQueryWrapper.like(SysRole::getRoleName, roleQueryDto.getRoleName());
        }
        if (roleQueryDto.getRoleCode() != null) {
            lambdaQueryWrapper.like(SysRole::getRoleCode, roleQueryDto.getRoleCode());
        }
        // 构造分页对象
        Page<SysRole> page = new Page<>(roleQueryDto.getPage(), roleQueryDto.getSize());
        // 查询后的结果
        Page<SysRole> sysRolePage = page(page, lambdaQueryWrapper);
        return new PageResult<SysRole>(sysRolePage.getTotal(), sysRolePage.getPages(), sysRolePage.getRecords());
    }

    /*@Override
    public PageResult<SysRole> getSysRoleByPageHelper(RoleQueryDto roleQueryDto) {
        // 开启分页
        PageHelper.startPage(roleQueryDto.getPage(), roleQueryDto.getSize());

        // 执行查询
        List<SysRole> sysRoleList = roleManageMapper.selectAll(roleQueryDto.getRoleName(), roleQueryDto.getRoleCode());

        // 封装结果
        PageInfo<SysRole> sysRolePageInfo = new PageInfo<>(sysRoleList);
        PageResult<SysRole> sysRolePageResult = new PageResult<>();
        sysRolePageResult.setRecords(sysRolePageInfo.getList());
        sysRolePageResult.setTotal(sysRolePageInfo.getTotal());
        sysRolePageResult.setPages(sysRolePageInfo.getPages());
        return sysRolePageResult;
    }*/

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysRole(List<Long> ids) {
        removeBatchByIds(ids);
    }

    @Override
    public void deleteSysRoleByMB(List<Long> ids) {
        roleManageMapper.deleteSysRoleByMB(ids);
    }

    @Override
    public void updateSysRoleByMB(SysRole sysRole) {
        roleManageMapper.updateSysRoleByMB(sysRole);
    }

    @Override
    public void updateSysRole(SysRole sysRole) {
        LambdaUpdateWrapper<SysRole> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(SysRole::getId, sysRole.getId());
        if (sysRole.getRoleName() != null && !sysRole.getRoleName().isEmpty()) {
            lambdaUpdateWrapper.set(SysRole::getRoleName, sysRole.getRoleName());
        }
        if (sysRole.getRoleCode() != null && !sysRole.getRoleCode().isEmpty()) {
            lambdaUpdateWrapper.set(SysRole::getRoleCode, sysRole.getRoleCode());
        }
        if (sysRole.getDescription() != null && !sysRole.getDescription().isEmpty()) {
            lambdaUpdateWrapper.set(SysRole::getDescription, sysRole.getDescription());
        }
        update(lambdaUpdateWrapper);
    }

    @Override
    public SysRole selectById(Long id) {
        return getById(id);
    }

    @Override
    public Map<String, List<SysRole>> listAllSysRole() {
        List<SysRole> sysRoleList = list();
        Map<String, List<SysRole>> listAllSysRole = new HashMap<>();
        listAllSysRole.put("allSysRole", sysRoleList);
        return listAllSysRole;
    }

}
