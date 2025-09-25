package com.cell.spzx.auth_server.feign;

import com.cell.model.entity.system.SysUser;
import com.cell.model.vo.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "spzx-authority-manage")
public interface SysUserFeignClient {
    @PostMapping("/sys_user/add")
    @Operation(summary = "在用户注册时新增系统用户", description = "当用户注册时在 sys_user 表中插入数据")
    Result add(@RequestBody SysUser sysUser);
}
