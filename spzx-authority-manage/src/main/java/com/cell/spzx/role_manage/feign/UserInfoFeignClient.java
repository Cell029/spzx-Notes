package com.cell.spzx.role_manage.feign;

import com.cell.model.entity.user.UserInfo;
import com.cell.model.vo.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
@FeignClient(name = "spzx-auth-server")
public interface UserInfoFeignClient {
    @GetMapping("/auth-server/user/userInfo")
    @Operation(summary = "获取用户信息", description = "通过 session 拿到用户 id 再查询数据库")
    Result<UserInfo> getUserInfo(HttpServletRequest request);
}
