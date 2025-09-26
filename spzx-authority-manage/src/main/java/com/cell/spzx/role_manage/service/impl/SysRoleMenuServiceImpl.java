package com.cell.spzx.role_manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cell.model.dto.system.SysRoleMenuDto;
import com.cell.model.entity.system.SysMenu;
import com.cell.model.entity.system.SysRoleMenu;
import com.cell.model.entity.system.SysRoleUser;
import com.cell.model.entity.system.SysUser;
import com.cell.model.entity.user.UserInfo;
import com.cell.model.vo.common.Result;
import com.cell.model.vo.system.SysMenuVo;
import com.cell.spzx.role_manage.feign.UserInfoFeignClient;
import com.cell.spzx.role_manage.mapper.SysRoleMenuMapper;
import com.cell.spzx.role_manage.service.RoleUserService;
import com.cell.spzx.role_manage.service.SysMenuService;
import com.cell.spzx.role_manage.service.SysRoleMenuService;
import com.cell.spzx.role_manage.service.SysUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private RoleUserService roleUserService;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

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
    public List<SysMenuVo> getUsableMenu(Long roleId) {
        // 查询 sys_role_menu，获取该角色所有的菜单 id
        List<Long> roleMenuIdList = list(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId)).stream().map(SysRoleMenu::getMenuId).toList();
        List<SysMenu> sysMenuList = sysMenuService.list(new LambdaQueryWrapper<SysMenu>().in(SysMenu::getId, roleMenuIdList));
        Map<Long, List<SysMenu>> sameParentIdMenu = sysMenuList.stream().collect(Collectors.groupingBy(SysMenu::getParentId));
        System.out.println("getUsableMenuWithSySMenu's sameParentIdMenu：" + sameParentIdMenu);
        List<SysMenu> usableMenuList = new ArrayList<>();
        // 如果这些子节点的 parentId 为 0，证明这些叶子节点就是一级节点，可以作为一级父节点
        if (sameParentIdMenu.containsKey(0L)) {
            usableMenuList.addAll(sameParentIdMenu.get(0L));
            // 删除一级节点
            sameParentIdMenu.remove(0L);
        }
        if (!sameParentIdMenu.isEmpty()) {
            List<SysMenu> allMenu = sysMenuService.getAllMenu();
            usableMenuList.addAll(getParentMenu(allMenu, sameParentIdMenu));
        }
        // 将 List<SysMenu> 转换成 List<SysMenuVo>
        return usableMenuList.stream().map(this::convertToVo).collect(Collectors.toList());
    }

    private SysMenuVo convertToVo(SysMenu sysMenu) {
        SysMenuVo sysMenuVo = new SysMenuVo();
        BeanUtils.copyProperties(sysMenu, sysMenuVo);
        // 子节点判空处理
        if (sysMenu.getChildren() != null && !sysMenu.getChildren().isEmpty()) {
            sysMenuVo.setChildren(
                    sysMenu.getChildren().stream()
                            .map(this::convertToVo)
                            .collect(Collectors.toList())
            );
        } else {
            // 子节点为空则存入空集合，避免 stream 遇到 null 报错
            sysMenuVo.setChildren(new ArrayList<>());
        }
        return sysMenuVo;
    }

    @Override
    public List<SysMenu> getUsableMenuWithSySMenu(Long roleId) {
        // 查询 sys_role_menu，获取该角色所有的菜单 id
        List<Long> roleMenuIdList = list(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId)).stream().map(SysRoleMenu::getMenuId).toList();
        List<SysMenu> sysMenuList = sysMenuService.list(new LambdaQueryWrapper<SysMenu>().in(SysMenu::getId, roleMenuIdList));
        Map<Long, List<SysMenu>> sameParentIdMenu = sysMenuList.stream().collect(Collectors.groupingBy(SysMenu::getParentId));
        System.out.println("getUsableMenuWithSySMenu's sameParentIdMenu：" + sameParentIdMenu);
        List<SysMenu> usableMenuList = new ArrayList<>();
        // 如果这些子节点的 parentId 为 0，证明这些叶子节点就是一级节点，可以作为一级父节点
        if (sameParentIdMenu.containsKey(0L)) {
            usableMenuList.addAll(sameParentIdMenu.get(0L));
            // 删除一级节点
            sameParentIdMenu.remove(0L);
        }
        if (!sameParentIdMenu.isEmpty()) {
            List<SysMenu> allMenu = sysMenuService.getAllMenu();
            usableMenuList.addAll(getParentMenu(allMenu, sameParentIdMenu));
        }
        return usableMenuList;
    }

    @Override
    public List<SysMenuVo> getDynamicMenu(HttpServletRequest request) {
        // 根据 userId 查找该用户的用户名或手机号，然后再查询 sys_user 获取到 SysUserId
        HttpSession session = request.getSession(false);
        Long userId = (Long) session.getAttribute("userId");
        Result<UserInfo> result = userInfoFeignClient.getUserInfoById(userId);
        UserInfo userInfo = result.getData();
        if (userInfo != null) {
            String username = userInfo.getUsername();
            String phone = userInfo.getPhone();
            SysUser sysUser = sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username).eq(SysUser::getPhone, phone));
            if (sysUser != null) {
                // 获取到 SysUserId 再查询 sys_user_role 表获取到该用户关联的 roleId
                Long sysUserId = sysUser.getId();
                SysRoleUser sysRoleUser = roleUserService.getOne(new LambdaQueryWrapper<SysRoleUser>().eq(SysRoleUser::getUserId, sysUserId));
                if (sysRoleUser != null) {
                    Long roleId = sysRoleUser.getRoleId();
                    return getUsableMenu(roleId);
                }
            }
        }
        return Collections.emptyList();
    }

    public List<SysMenu> getParentMenu(List<SysMenu> parentMenuList, Map<Long, List<SysMenu>> sameParentIdMenu) {
        List<SysMenu> newSysMenuList = new ArrayList<>(); // 新当前节点集合
        // 新当前节点的孩子节点集合
        for (SysMenu sysMenu : parentMenuList) {
            List<SysMenu> newChildSysMenuList = new ArrayList<>();
            // 当叶子节点全部消耗完，直接结束递归
            if (sameParentIdMenu.isEmpty()) {
                return newSysMenuList;
            }
            SysMenu newSysMenu = new SysMenu();
            // 拷贝当前节点主要是为了给新节点赋值新的孩子节点
            BeanUtils.copyProperties(sysMenu, newSysMenu);
            Long key = sysMenu.getId();
            // 如果 map 集合中包含父节点的 Id，那么证明 value 就是这些父节点的孩子节点
            if (sameParentIdMenu.containsKey(key)) {
                newChildSysMenuList.addAll(sameParentIdMenu.get(key));
                sameParentIdMenu.remove(key);
            }
            // 判断当前节点是否有孩子节点
            if (sysMenu.getChildren() != null && !sysMenu.getChildren().isEmpty()) {
                // 把当前节点的孩子节点传递过去，判断下一层的节点的 id 是否为叶子节点的 parentId
                List<SysMenu> childMenuList = getParentMenu(sysMenu.getChildren(), sameParentIdMenu);
                if (childMenuList != null && !childMenuList.isEmpty()) {
                    newChildSysMenuList.addAll(childMenuList);
                }
            } else {
                continue;
            }
            if (!newChildSysMenuList.isEmpty()) {
                // 给当前新节点添加孩子节点
                newSysMenu.setChildren(newChildSysMenuList);
                // 把当前新节点添加进集合，准备作为其它节点的孩子节点
                newSysMenuList.add(newSysMenu);
            }
        }
        // System.out.println("当前集合数据：" + newSysMenuList);
        return newSysMenuList;
    }


}
