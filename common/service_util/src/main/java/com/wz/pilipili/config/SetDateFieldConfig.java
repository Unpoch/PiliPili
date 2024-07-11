package com.wz.pilipili.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

/**
 * 表 create_time和update_time 插入时自动填充配置
 */
@Configuration
public class SetDateFieldConfig implements MetaObjectHandler {

    //创建时间的方法
    @Override
    public void insertFill(MetaObject metaObject) {
        // 为实体类中的更新时间/创建时间创建初始化时间
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }

    //更新时间的方法
    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateDate", new Date(), metaObject);
    }
}
