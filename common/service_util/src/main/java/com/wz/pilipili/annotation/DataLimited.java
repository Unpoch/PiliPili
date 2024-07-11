package com.wz.pilipili.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 数据权限 注解类
 */
@Retention(RetentionPolicy.RUNTIME) //运行阶段
@Target(ElementType.METHOD)  //注解的位置
@Documented
@Component
public @interface DataLimited {

}
