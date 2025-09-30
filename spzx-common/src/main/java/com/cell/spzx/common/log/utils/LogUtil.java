package com.cell.spzx.common.log.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.shaded.io.grpc.netty.shaded.io.netty.handler.codec.http.HttpMethod;
import com.cell.model.entity.system.SysOperLog;
import com.cell.spzx.common.log.annotation.Log;
import com.cell.spzx.common.utils.AuthContextUtil;
import com.cell.spzx.common.utils.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;

public class LogUtil {

    // 操作执行之前调用
    public static void beforeHandleLog(Log sysLog, ProceedingJoinPoint joinPoint, SysOperLog sysOperLog) {
        // 1. 设置操作模块名称
        sysOperLog.setTitle(sysLog.title());
        // 2. 设置操作人员类别
        sysOperLog.setOperatorType(sysLog.operatorType().name());
        // 3. 设置方法名称
        // 获取目标方法信息
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature() ;
        Method method = methodSignature.getMethod();
        sysOperLog.setMethod(method.getDeclaringClass().getName());
        // 获取当前请求相关数据
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            // 4. 设置请求方式
            sysOperLog.setRequestMethod(request.getMethod());
            // 5. 设置请求 URL
            sysOperLog.setOperUrl(request.getRequestURI());
            // 6. 设置请求 IP
            sysOperLog.setOperIp(IpUtil.getIpAddress(request));
        }
        // 7. 设置请求参数
        if(sysLog.isSaveRequestData()) {
            String requestMethod = sysOperLog.getRequestMethod();
            if (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod) || HttpMethod.DELETE.name().equals(requestMethod)) {
                String businessType = sysLog.businessType();
                if (businessType != null && (businessType.equals("PUT") || businessType.equals("POST") || businessType.equals("DELETE"))) {
                    String params = Arrays.toString(joinPoint.getArgs());
                    sysOperLog.setOperParam(params);
                }
            }
        }
        // 8. 设置操作人员
        sysOperLog.setOperName(AuthContextUtil.get() == null ? "" : AuthContextUtil.get().toString());
    }

    // 操作执行之后调用
    public static void afterHandleLog(Log sysLog, Object proceed, SysOperLog sysOperLog, int status, String errorMsg) {
        if(sysLog.isSaveResponseData()) {
            // 9. 设置接口的返回结果
            sysOperLog.setJsonResult(JSON.toJSONString(proceed));
        }
        // 10. 设置操作状态
        sysOperLog.setStatus(status);
        // 11. 设置错误信息
        sysOperLog.setErrorMsg(errorMsg);
    }
}
