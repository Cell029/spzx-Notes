package com.cell.spzx.role_manage.controller;

import com.cell.model.dto.h5.SysMenuDto;
import com.cell.model.dto.system.SysRoleMenuDto;
import com.cell.model.entity.system.SysMenu;
import com.cell.model.vo.common.Result;
import com.cell.model.vo.common.ResultCodeEnum;
import com.cell.model.vo.system.SysMenuVo;
import com.cell.spzx.role_manage.service.SysMenuService;
import com.cell.spzx.role_manage.service.SysRoleMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/roleMenu")
@Tag(name = "SysRoleMenuController", description = "角色菜单关联相关接口")
public class SysRoleMenuController {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @GetMapping("/getRoleHasMenu/{id}")
    @Operation(summary = "展示当前操作角色当前选中的所有菜单")
    public Result getRoleHasMenu(@PathVariable("id") Long roleId) {
        List<Long> menuIdList = sysRoleMenuService.getRoleHasMenu(roleId);
        return Result.build(menuIdList, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("/usableMenu/{id}")
    @Operation(summary = "展示某个角色可以使用的菜单(返回值为 SysMenuVo 集合)", description = "不同角色有不同的功能，因此他们能操控的菜单也不同，需要限制查询条件")
    public Result getUsableMenu(@PathVariable("id") Long roleId) {
        List<SysMenuVo> usableMenuList = sysRoleMenuService.getUsableMenu(roleId);
        return Result.build(usableMenuList, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("/usableMenuWithSySMenu/{id}")
    @Operation(summary = "展示某个角色可以使用的菜单(返回值为 SysMenu 集合)", description = "不同角色有不同的功能，因此他们能操控的菜单也不同，需要限制查询条件")
    public Result getUsableMenuWithSySMenu(@PathVariable("id") Long roleId) {
        List<SysMenu> usableMenuList = sysRoleMenuService.getUsableMenuWithSySMenu(roleId);
        return Result.build(usableMenuList, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("/getDynamicMenu")
    @Operation(summary = "根据当前登录用户动态展示数据", description = "通过登录用户查找该用户对应的角色再动态展示菜单")
    public Result getDynamicMenu(HttpServletRequest request) {
        List<SysMenuVo> dynamicMenu = sysRoleMenuService.getDynamicMenu(request);
        return Result.build(dynamicMenu, ResultCodeEnum.SUCCESS);
    }

    @PostMapping("/menuAllocation")
    @Operation(summary = "给某个角色分配菜单", description = "先展示所有的菜单，当选中后将数据添加进数据库并删除旧数据")
    public Result menuAllocation(@RequestBody SysRoleMenuDto sysRoleMenuDto) {
        sysRoleMenuService.menuAllocation(sysRoleMenuDto);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }
}
