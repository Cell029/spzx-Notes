package com.cell.spzx.role_manage.controller;

import com.cell.model.dto.system.SysUserAssoRoleDto;
import com.cell.model.entity.system.SysRole;
import com.cell.model.entity.system.SysRoleUser;
import com.cell.model.vo.common.Result;
import com.cell.model.vo.common.ResultCodeEnum;
import com.cell.spzx.role_manage.service.RoleUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/roleUser")
@Tag(name = "RoleUserController", description = "系统用户关联角色功能相关接口")
public class RoleUserController {

    @Autowired
    private RoleUserService roleUserService;

    @PutMapping("/assignRoles")
    @Operation(summary = "给系统用户分配角色", description = "一个系统用户可以选择多个角色，把它们的关系保存进 sys_user_role 表")
    public Result assignRoles(@RequestBody SysUserAssoRoleDto sysUserAssoRoleDto) {
        roleUserService.assignRoles(sysUserAssoRoleDto);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("/getUserRoleData/{id}")
    @Operation(summary = "回显当前系统用户所属的角色", description = "点击分配按钮时应该展示该用户当前拥有的所有角色")
    public Result getUserRoleData(@PathVariable("id") Integer id) {
        Map<String, List<SysRole>> userHasRole =  roleUserService.getUserRoleData(id);
        return Result.build(userHasRole, ResultCodeEnum.SUCCESS);
    }

}
