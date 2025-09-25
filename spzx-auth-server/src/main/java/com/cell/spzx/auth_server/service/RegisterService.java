package com.cell.spzx.auth_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cell.model.dto.h5.UserRegisterDto;
import com.cell.model.entity.user.UserInfo;
import com.cell.model.vo.common.Result;

public interface RegisterService extends IService<UserInfo> {

    Result<Boolean> register(UserRegisterDto UserRegisterDto);

}
