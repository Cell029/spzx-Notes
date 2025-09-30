package com.cell.spzx.common.log.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cell.model.entity.system.SysOperLog;
import com.cell.spzx.common.log.mapper.SysOperLogMapper;
import com.cell.spzx.common.log.service.SysOperLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements SysOperLogService {

    @Async
    @Override
    public void saveSysOperLog(SysOperLog sysOperLog) {
        System.out.println(Thread.currentThread().getName());
        save(sysOperLog);
    }

}
