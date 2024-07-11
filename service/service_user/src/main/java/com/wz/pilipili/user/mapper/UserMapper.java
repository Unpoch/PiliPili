package com.wz.pilipili.user.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wz.pilipili.entity.user.User;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-05-26
 */
public interface UserMapper extends BaseMapper<User> {

    /*
     * 根据手机号或者邮箱查询用户
     */
    public User getUserByPhoneOrEmail(String phoneOrEmail);
}
