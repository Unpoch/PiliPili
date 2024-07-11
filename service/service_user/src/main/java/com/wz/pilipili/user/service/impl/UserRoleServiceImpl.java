package com.wz.pilipili.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wz.pilipili.entity.auth.UserRole;
import com.wz.pilipili.user.mapper.UserRoleMapper;
import com.wz.pilipili.user.service.AuthRoleService;
import com.wz.pilipili.user.service.UserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户角色关联表 服务实现类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-02
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    /**
     * 根据用户id获取其关联的角色列表
     * 这里涉及到t_user_role和t_auth_role两张表
     * UserRole的四个字段：userId,roleId,roleName,roleCode
     * 其中roleName和roleCode字段再t_auth_role表中
     * 因此需要两表联查 -> 需要到xml中写SQL语句
     */
    @Override
    public List<UserRole> getUserRoleListByUserId(Long userId) {
        return userRoleMapper.getUserRoleListByUserId(userId);
    }

    /**
     * 添加用户角色
     */
    @Override
    public void addUserRole(UserRole userRole) {
        this.save(userRole);
    }
}
