package com.cell.spzx.role_manage.feign;

import com.cell.model.entity.user.UserInfo;
import com.cell.model.vo.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@FeignClient(name = "spzx-auth-server")
public interface UserInfoFeignClient {
    @GetMapping("/auth-server/user/userInfo/{id}")
    @Operation(summary = "根据用户 id 获取用户信息")
    Result<UserInfo> getUserInfoById(@PathVariable("id") Long id);
}
