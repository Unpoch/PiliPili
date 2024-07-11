package com.wz.pilipili.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wz.pilipili.entity.auth.UserRole;

import java.util.List;

/**
 * <p>
 * 用户角色关联表 Mapper 接口
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-02
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {

    //根据userId查询关联的角色列表
    List<UserRole> getUserRoleListByUserId(Long userId);
}
