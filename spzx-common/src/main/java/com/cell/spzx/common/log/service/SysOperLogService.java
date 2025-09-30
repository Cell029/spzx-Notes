package com.cell.spzx.common.log.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cell.model.entity.system.SysOperLog;

public interface SysOperLogService extends IService<SysOperLog> {

    void saveSysOperLog(SysOperLog sysOperLog);

}
