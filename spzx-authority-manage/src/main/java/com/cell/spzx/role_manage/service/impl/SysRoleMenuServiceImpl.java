package com.cell.spzx.role_manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cell.model.dto.system.SysRoleMenuDto;
import com.cell.model.entity.system.SysMenu;
import com.cell.model.entity.system.SysRoleMenu;
import com.cell.model.vo.system.SysMenuVo;
import com.cell.spzx.role_manage.mapper.SysRoleMenuMapper;
import com.cell.spzx.role_manage.service.SysMenuService;
import com.cell.spzx.role_manage.service.SysRoleMenuService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service("sysRoleMenuService")
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

    @Autowired
    private SysMenuService sysMenuService;

    @Override
    @Transactional
    public void menuAllocation(SysRoleMenuDto sysRoleMenuDto) {
        // 先删除数据库中该角色之前被分配的菜单
        Long roleId = sysRoleMenuDto.getRoleId();
        remove(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        // 遍历 menuId，把 roleId 与它进行关联
        List<SysRoleMenu> sysRoleMenuList = new ArrayList<>();
        sysRoleMenuDto.getMenuIdList().forEach(menuId -> {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(roleId);
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenuList.add(sysRoleMenu);
        });
        saveBatch(sysRoleMenuList);
    }

    @Override
    public List<Long> getRoleHasMenu(Long roleId) {
        List<SysRoleMenu> sysRoleMenuList = list(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        List<Long> menuIdList = sysRoleMenuList.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        return menuIdList;
    }

    @Override
    public List<SysMenuVo> getUsableMenu(Long roleId) { // 查询 sys_role_menu
        List<SysRoleMenu> sysRoleMenuList = list(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        List<Long> menuIdList = sysRoleMenuList.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        // 当前保存的都是叶子节点，即最小层级的菜单，要想展示所有的菜单结构，就需要寻找父节点
        List<SysMenuVo> sysMenuVoList = new ArrayList<>();
        for (Long menuId : menuIdList) {
            SysMenuVo sysMenuVo = new SysMenuVo();
            SysMenu parentMenu = getParentMenu(menuId);
            BeanUtils.copyProperties(parentMenu, sysMenuVo);
            sysMenuVoList.add(sysMenuVo);
        }
        return sysMenuVoList;
    }

    private SysMenu getParentMenu(Long menuId) {
        SysMenu sysMenu = sysMenuService.getById(menuId);
        Long parentMenuId = sysMenu.getParentId();
        // 当前节点是一级菜单
        if (parentMenuId == 0L) {
            SysMenu parentMenu = sysMenuService.getOne(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, sysMenu.getParentId()));
            Long menuParentId = parentMenu.getParentId();
            return parentMenu;
            return sysMenu;
        } else {
            SysMenu parentMenu = sysMenuService.getOne(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getId, parentMenuId));
            if (parentMenu == null) {
                parentMenu.setChildren();
            }
        }
    }


}
