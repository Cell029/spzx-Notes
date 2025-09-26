package com.cell.spzx.role_manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cell.model.dto.h5.SysMenuDto;
import com.cell.model.entity.system.SysMenu;
import com.cell.model.vo.common.ResultCodeEnum;
import com.cell.spzx.role_manage.mapper.SysMenuMapper;
import com.cell.spzx.role_manage.service.SysMenuService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service("sysMenuService")
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    /*@Override
    public List<SysMenu> getMenu() {
        List<SysMenu> sysMenuList = new ArrayList<>();
        // 查询 parentId 为 0 的数据，这些就是父菜单
        List<SysMenu> parentMenuList = list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, 0L));
        for (SysMenu parentMenu : parentMenuList) {
            SysMenu sysMenu = getChildMenu(parentMenu);
            sysMenuList.add(sysMenu);
        }
        return sysMenuList;
    }

    // 父节点传入子节点，再遍历每个子节点获取它的子节点，直到某个节点不再拥有子节点
    private SysMenu getChildMenu(SysMenu parentMenu) {
        // 获取到父菜单的子菜单，查询那些 parenId 为父菜单 id 的数据
        // select * from sys_menu where parentId = ?
        List<SysMenu> childrenMenuList = list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, parentMenu.getId()));
        if (childrenMenuList != null && !childrenMenuList.isEmpty()) {
            parentMenu.setChildren(childrenMenuList);
        }
        // 如果该节点有孩子节点，那就继续查询该节点的孩子节点，看该节点的孩子节点是否也有孩子节点
        if (parentMenu.getChildren() != null && !parentMenu.getChildren().isEmpty()) {
            // 遍历当前节点的子节点是否有子节点，递归调用本方法，只要没有数据的 parentId 为该节点的字节点的 id，那么就结束递归
            parentMenu.getChildren().forEach(this::getChildMenu);
        }
        return parentMenu;
    }*/

    @Override
    public List<SysMenu> getAllMenu() {
        List<SysMenu> sysMenuList = list();
        // 让这些菜单集合根据 parenId 进行分组
        Map<Long, List<SysMenu>> map = sysMenuList.stream().collect(Collectors.groupingBy(SysMenu::getParentId));
        // 从顶层菜单开始递归，因此先传入的 parentId 为 0
        return getChildMenu(0L, map);
    }

    @Override
    public void addSysMenu(SysMenu sysMenu) {
        Long parentId = sysMenu.getParentId();
        if (parentId == null) {
            sysMenu.setParentId(0L);
        }
        save(sysMenu);
    }

    @Override
    public SysMenu getMenuById(Long id) {
        return getById(id);
    }

    @Override
    public void updateSysMenu(SysMenuDto sysMenudto) {
        SysMenu sysMenu = new SysMenu();
        BeanUtils.copyProperties(sysMenudto, sysMenu);
        updateById(sysMenu);
    }

    @Override
    public void deleteSysMenu(Long id) {
        long count = count(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, id));
        // 如果当前要删除的菜单有子菜单，那么就不能进行删除
        if (count > 0) {
            throw new RuntimeException(ResultCodeEnum.NODE_ERROR.getMessage());
        }
        removeById(id);
    }

    // 父节点传入子节点，再遍历每个子节点获取它的子节点，直到某个节点不再拥有子节点
    private List<SysMenu> getChildMenu(Long parentId, Map<Long, List<SysMenu>> map) {
        // 获取父节点下的子节点列表
        List<SysMenu> childMenuList = map.get(parentId);
        if (childMenuList != null) {
            // 获取到父菜单的子菜单，查询那些 parenId 为父菜单 id 的数据
            for (SysMenu sysMenu : childMenuList) {
                List<SysMenu> nextChildMenuList = getChildMenu(sysMenu.getId(), map);
                // 当返回的孩子节点集合不为空，证明当前子节点是有孩子的，所以给它的 children 字段赋值
                sysMenu.setChildren(nextChildMenuList);
            }
        } else {
            // 当指定的 parentId 下的子节点为空，那么就返回一个空集合，让 menuId 为该 parentId 的 SysMenu 实体类的 children 字段赋值为空集合，
            // 即代表没有孩子
            return Collections.emptyList();
        }
        // 当孩子节点为空，证明当前 parentId 指向的那个 menuId 就是最小子节点
        // 因为根据 parentId 查询出的菜单列表为空，所以上一次调用该方法的那个节点的子节点为空，那么就会返回一个空集合
        return childMenuList;
    }
}
