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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/sys_user")
@Tag(name = "SysUserController", description = "系统用户管理相关接口")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/add")
    @Operation(summary = "在用户注册时新增系统用户", description = "当用户注册时在 sys_user 表中插入数据")
    public Result add(@RequestBody SysUser sysUser) {
        sysUserService.add(sysUser);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @PostMapping("/addSysUser")
    @Operation(summary = "新增系统用户", description = "在 sys_user 表中插入数据，同时将存储在 Minio 中临时目录的头像转移到固定目录")
    public Result addSysUser(@RequestBody SysUser sysUser) {
        sysUserService.addSysUser(sysUser);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @PutMapping("/updateSysUser")
    @Operation(summary = "修改系统用户", description = "修改数据的同时检查是否更新头像，如果更新则删除保存在 Minio 中的旧头像数据")
    public Result updateSysUser(@RequestBody SysUser sysUser) {
        sysUserService.updateSysUse(sysUser);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @PostMapping("/selectSysUserById/{id}")
    @Operation(summary = "修改系统用户时的回显数据", description = "点击修改按钮时会触发该接口显示当前用户的详细信息")
    public Result selectSysUserById(@PathVariable("id") Long id) {
        SysUser sysUser = sysUserService.selectSysUserById(id);
        return Result.build(sysUser, ResultCodeEnum.SUCCESS);
    }

    @PostMapping("/selectSysUserPage")
    @Operation(summary = "查询所有系统用户", description = "分页查询所有系统用户")
    public Result selectSysUserPage(@RequestBody SysUserQueryDto sysUserQueryDto) {
        PageResult<SysUser> sysUserPageResult = sysUserService.selectSysUserPage(sysUserQueryDto);
        return Result.build(sysUserPageResult, ResultCodeEnum.SUCCESS);
    }

    @PutMapping("/uploadAvatar")
    @Operation(summary = "上传头像", description = "上传头像到 Minio")
    public Result uploadAvatar(@RequestParam("file") MultipartFile file) {
        String fileUrl = sysUserService.uploadAvatar(file);
        return Result.build(fileUrl, ResultCodeEnum.SUCCESS);
    }

    @DeleteMapping("/deleteSysUser/{id}")
    @Operation(summary = "删除系统用户", description = "根据传递的用户 id 进行删除操作，同时删除保存在 Minio 中的头像")
    public Result deleteSysUser(@PathVariable("id") Long id) {
        sysUserService.deleteSysUser(id);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }



}
