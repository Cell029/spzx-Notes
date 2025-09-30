package com.cell.spzx.common.log.aspect;

import com.cell.model.entity.system.SysOperLog;
import com.cell.spzx.common.log.annotation.Log;
import com.cell.spzx.common.log.constant.LogStatusConstant;
import com.cell.spzx.common.log.enums.OperatorType;
import com.cell.spzx.common.log.service.SysOperLogService;
import com.cell.spzx.common.log.utils.LogUtil;
import com.cell.spzx.common.schedule.MinioSchedule;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Aspect
@Component
public class LogAspect {

    // 配置 log
    private static final Logger logger = Logger.getLogger(LogAspect.class.getName());

    @Autowired
    private SysOperLogService sysOperLogService;

    // 拦截所有类的所有方法中使用了 @Log 注解的方法
    @Pointcut("execution(* *(..)) && @annotation(com.cell.spzx.common.log.annotation.Log)")
    public void autoFillPointCut() {
    }

    @Around("autoFillPointCut()")
    public Object doAroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println(Thread.currentThread().getName());
        SysOperLog sysOperLog = new SysOperLog();
        // 获取到当前被拦截的方法上的 @Log 注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Log sysLog = signature.getMethod().getAnnotation(Log.class);
        // 调用 LogUtil 的前置方法
        LogUtil.beforeHandleLog(sysLog, joinPoint, sysOperLog);
        Object proceed = null;
        try {
            // 执行目标方法
            proceed = joinPoint.proceed();
            // 执行业务方法
            LogUtil.afterHandleLog(sysLog, proceed, sysOperLog, LogStatusConstant.SUCCESS, null);
            // 构建响应结果参数
        } catch (Throwable e) {
            // 业务方法执行产生异常，打印异常信息
            e.printStackTrace();
            LogUtil.afterHandleLog(sysLog, proceed, sysOperLog, LogStatusConstant.FAILURE, e.getMessage());
            throw e;
        } finally {
            // 保存日志数据，放在 try-catch 外，确保也能将异常信息添加进数据库
            sysOperLogService.saveSysOperLog(sysOperLog);
        }
        // 返回执行结果
        return proceed;
    }
}
