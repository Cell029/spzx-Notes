package com.cell.spzx.role_manage.controller;

import com.cell.model.dto.h5.RoleQueryDto;
import com.cell.model.entity.system.SysRole;
import com.cell.model.vo.common.Result;
import com.cell.model.vo.common.ResultCodeEnum;
import com.cell.model.vo.h5.PageResult;
import com.cell.spzx.role_manage.service.RoleManageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/role-manage")
@Tag(name = "RoleManageController", description = "角色管理相关接口")
public class RoleManageController {

    @Autowired
    private RoleManageService roleManageService;

    @PutMapping("/addRole")
    @Operation(summary = "新增角色", description = "在 sys_role 表中插入一条数据")
    public Result addSysRole(@RequestBody SysRole sysRole) {
        roleManageService.addSysRole(sysRole);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @PostMapping("/listRole")
    @Operation(summary = "分页查询所有角色", description = "分页查询所有角色")
    public Result listSysRole(@RequestBody RoleQueryDto  roleQueryDto) {
        PageResult<SysRole> sysRoleByPage = roleManageService.getSysRoleByPage(roleQueryDto);
        return Result.build(sysRoleByPage, ResultCodeEnum.SUCCESS);
    }

    @PostMapping("/listAllRole")
    @Operation(summary = "查询所有角色", description = "将查询出的所有角色封装成集合")
    public Result listAllSysRole() {
        Map<String, List<SysRole>> allSysRole = roleManageService.listAllSysRole();
        return Result.build(allSysRole, ResultCodeEnum.SUCCESS);
    }

    /*@PostMapping("/listRoleByPageHelper")
    @Operation(summary = "用 PageHelper 查询所有角色", description = "分页查询所有角色")
    public Result listSysRoleByPageHelper(@RequestBody RoleQueryDto  roleQueryDto) {
        PageResult<SysRole> sysRoleByPage = roleManageService.getSysRoleByPageHelper(roleQueryDto);
        return Result.build(sysRoleByPage, ResultCodeEnum.SUCCESS);
    }*/

    @DeleteMapping("/deleteRole")
    @Operation(summary = "删除角色", description = "可以选择删除单个数据或批量删除")
    public Result deleteSysRole(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.build(null, ResultCodeEnum.DATA_ERROR);
        }
        roleManageService.deleteSysRole(ids);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @DeleteMapping("/deleteRoleByMB")
    @Operation(summary = "使用 MyBatis 删除角色", description = "可以选择删除单个数据或批量删除")
    public Result deleteSysRoleByMB(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.build(null, ResultCodeEnum.DATA_ERROR);
        }
        roleManageService.deleteSysRoleByMB(ids);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @PutMapping("/updateSysRole")
    @Operation(summary = "修改角色", description = "根据传递的数据进行选择性修改")
    public Result updateSysRole(@RequestBody SysRole sysRole) {
        roleManageService.updateSysRole(sysRole);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @PutMapping("/updateSysRoleByMB")
    @Operation(summary = "使用 MyBatis 修改角色", description = "根据传递的数据进行选择性修改")
    public Result updateSysRoleByMB(@RequestBody SysRole sysRole) {
        roleManageService.updateSysRoleByMB(sysRole);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("/selectById/{id}")
    @Operation(summary = "根据 Id 查询角色", description = "修改角色信息时需要回显原有的数据，也就是一次查询操作")
    public Result selectById(@PathVariable Long id) {
        SysRole sysRole = roleManageService.selectById(id);
        return Result.build(sysRole, ResultCodeEnum.SUCCESS);
    }

}
