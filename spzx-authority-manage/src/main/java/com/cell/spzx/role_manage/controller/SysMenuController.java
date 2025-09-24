package com.cell.spzx.role_manage.controller;

import com.cell.model.dto.h5.SysMenuDto;
import com.cell.model.entity.system.SysMenu;
import com.cell.model.vo.common.Result;
import com.cell.model.vo.common.ResultCodeEnum;
import com.cell.model.vo.system.SysMenuVo;
import com.cell.spzx.role_manage.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sysMenu")
@Tag(name = "SysMenuController", description = "系统菜单管理相关接口")
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;

    @GetMapping("/selectAllMenu")
    @Operation(summary = "展示所有菜单", description = "展示所有的菜单列表")
    public Result getAllMenu() {
        List<SysMenu> sysMenuList = sysMenuService.getAllMenu();
        return Result.build(sysMenuList, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("/selectMenuById/{id}")
    @Operation(summary = "回显单个菜单数据", description = "修改菜单时需要先回显数据")
    public Result getMenuById(@PathVariable("id") Long id) {
        SysMenu sysMenu = sysMenuService.getMenuById(id);
        return Result.build(sysMenu, ResultCodeEnum.SUCCESS);
    }

    @PostMapping("/addMenu")
    @Operation(summary = "新增菜单", description = "在 sys_menu 表中插入数据，如果插入的是子菜单，则需要指定父菜单 id")
    public Result addSysMenu(@RequestBody SysMenu sysMenu) {
        sysMenuService.addSysMenu(sysMenu);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @PutMapping("/updateMenu")
    @Operation(summary = "修改菜单", description = "前端表单填写数据后，后端直接进行更新数据库操作")
    public Result updateSysMenu(@RequestBody SysMenuDto sysMenuDto) {
        sysMenuService.updateSysMenu(sysMenuDto);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @DeleteMapping("/deleteMenu/{id}")
    @Operation(summary = "删除菜单", description = "前端点击删除按钮后，后端接收到该菜单的 id 后对数据库进行修改")
    public Result deleteSysMenu(@PathVariable("id") Long id) {
        sysMenuService.deleteSysMenu(id);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }



}
