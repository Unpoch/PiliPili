package com.wz.pilipili.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 接口权限 注解类
 */
@Retention(RetentionPolicy.RUNTIME) //运行阶段
@Target(ElementType.METHOD)  //注解的位置
@Documented
@Component
public @interface ApiLimitedRole {

    //注解的参数：角色的编码列表
    String[] limitedRoleCodeList() default {};
}
