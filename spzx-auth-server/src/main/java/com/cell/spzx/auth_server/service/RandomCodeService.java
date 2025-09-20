package com.cell.spzx.auth_server.service;

import com.cell.model.vo.system.ValidateCodeVo;

public interface RandomCodeService {
    ValidateCodeVo generateRandomCode(String randomCodeKey);
}
