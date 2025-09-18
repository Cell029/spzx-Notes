package com.cell.spzx.auth_server.service;

import com.baomidou.mybatisplus.extension.service.IService;

public interface PhoneCodeService{

    Boolean generatePhoneCode(String phone);

}
