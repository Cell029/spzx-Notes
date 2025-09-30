package com.cell.spzx.common.log.annotation;

import com.cell.spzx.common.log.enums.OperatorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义操作日志记录注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    // 模块名称
    String title();

    // 操作人员类别
    OperatorType operatorType() default OperatorType.MANAGE;

    // 业务类型（0其它 1新增 2修改 3删除）
    String businessType();

    // 是否保存请求的参数
    boolean isSaveRequestData() default true;

    // 是否保存响应的参数
    boolean isSaveResponseData() default true;
}
