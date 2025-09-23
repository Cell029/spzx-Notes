package com.cell.spzx.role_manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cell.model.dto.system.SysUserAssoRoleDto;
import com.cell.model.entity.system.SysRole;
import com.cell.model.entity.system.SysRoleUser;
import com.cell.spzx.role_manage.mapper.RoleUserMapper;
import com.cell.spzx.role_manage.service.RoleManageService;
import com.cell.spzx.role_manage.service.RoleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("roleUserService")
public class RoleUserServiceImpl extends ServiceImpl<RoleUserMapper, SysRoleUser> implements RoleUserService {

    @Autowired
    private RoleManageService roleManageService;

    @Override
    @Transactional
    public void assignRoles(SysUserAssoRoleDto sysUserAssoRoleDto) {
        // 删除之前关联的所有角色
        remove(new LambdaQueryWrapper<SysRoleUser>().eq(SysRoleUser::getUserId, sysUserAssoRoleDto.getUserId()));
        // 再新增新的关联角色
        List<Long> roleIdList = sysUserAssoRoleDto.getRoleIdList();
        ArrayList<SysRoleUser> sysRoleUserList = new ArrayList<>();
        roleIdList.forEach(roleId->{
            SysRoleUser sysRoleUser = new SysRoleUser();
            sysRoleUser.setUserId(sysUserAssoRoleDto.getUserId());
            sysRoleUser.setRoleId(roleId);
            sysRoleUserList.add(sysRoleUser);
        });
        saveBatch(sysRoleUserList);
    }

    @Override
    public Map<String, List<SysRole>> getUserRoleData(Integer id) {
        List<SysRoleUser> sysRoleUserList = list(new LambdaQueryWrapper<SysRoleUser>().eq(SysRoleUser::getUserId, id));
        // 把当前用户的角色 id 封装成集合
        List<Long> roleIds = sysRoleUserList.stream().map(SysRoleUser::getRoleId).collect(Collectors.toList());
        // 执行批量查询
        List<SysRole> sysRoleList = roleManageService.listByIds(roleIds);
        HashMap<String, List<SysRole>> userHasRole = new HashMap<>();
        userHasRole.put("userHasRole", sysRoleList);
        return userHasRole;
    }

}
