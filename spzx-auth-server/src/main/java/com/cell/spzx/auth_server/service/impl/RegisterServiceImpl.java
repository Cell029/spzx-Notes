package com.cell.spzx.auth_server.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cell.model.dto.h5.UserLoginDto;
import com.cell.model.dto.h5.UserRegisterDto;
import com.cell.model.entity.system.SysUser;
import com.cell.model.entity.user.UserInfo;
import com.cell.model.vo.common.Result;
import com.cell.model.vo.common.ResultCodeEnum;
import com.cell.spzx.auth_server.mapper.RegisterMapper;
import com.cell.spzx.auth_server.service.RegisterService;
import com.cell.spzx.common.constant.LoginConstant;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service("registerService")
public class RegisterServiceImpl extends ServiceImpl<RegisterMapper, UserInfo> implements RegisterService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result<Boolean> register(UserRegisterDto userRegisterDto) {
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userRegisterDto, userInfo);

        // 查询是否存在相同用户名或手机号的用户
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotEmpty(userInfo.getUsername())) {
            wrapper.eq(UserInfo::getUsername, userInfo.getUsername());
        }
        if (StrUtil.isNotEmpty(userInfo.getPhone())) {
            wrapper.or(w -> w.eq(UserInfo::getPhone, userInfo.getPhone()));
        }

        UserInfo checkExist = getOne(wrapper);
        if (checkExist != null) {
            return Result.build(false, ResultCodeEnum.USER_ALREADY_EXIST.getCode(), ResultCodeEnum.USER_ALREADY_EXIST.getMessage());
        }

        // 验证手机验证码
        String value = stringRedisTemplate.opsForValue().get(LoginConstant.LOGIN_PHONE_CODE_KEY + userInfo.getPhone());
        if (StrUtil.isEmpty(value)) {
            return Result.build(false, ResultCodeEnum.VALIDATE_CODE_ERROR.getCode(), ResultCodeEnum.VALIDATE_CODE_ERROR.getMessage());
        }

        String code = value.split("_")[0];
        if (StrUtil.isEmpty(code) || !code.equals(userRegisterDto.getCode())) {
            return Result.build(false, ResultCodeEnum.VALIDATE_CODE_ERROR.getCode(), ResultCodeEnum.VALIDATE_CODE_ERROR.getMessage());
        }

        // 满足条件，进行注册
        save(userInfo);
        stringRedisTemplate.delete(LoginConstant.LOGIN_PHONE_CODE_KEY + userInfo.getPhone());
        return Result.build(true, ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage());
    }

}
