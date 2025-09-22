package com.cell.spzx.role_manage.controller;

import com.cell.model.dto.h5.SysUserQueryDto;
import com.cell.model.entity.system.SysUser;
import com.cell.model.vo.common.Result;
import com.cell.model.vo.common.ResultCodeEnum;
import com.cell.model.vo.h5.PageResult;
import com.cell.spzx.role_manage.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sys_user")
@Tag(name = "SysUserController", description = "系统用户管理相关接口")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/addSysUser")
    @Operation(summary = "新增系统用户", description = "在 sys_user 表中插入数据")
    public Result addSysUser(@RequestBody SysUser sysUser) {
        sysUserService.addSysUser(sysUser);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @PostMapping("/selectSysUserPage")
    @Operation(summary = "查询所有系统用户", description = "分页查询所有系统用户")
    public Result selectSysUserPage(@RequestBody SysUserQueryDto sysUserQueryDto) {
        PageResult<SysUser> sysUserPageResult = sysUserService.selectSysUserPage(sysUserQueryDto);
        return Result.build(sysUserPageResult, ResultCodeEnum.SUCCESS);
    }

}
