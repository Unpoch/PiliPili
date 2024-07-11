package com.wz.pilipili.user.service.impl;

import com.wz.pilipili.entity.auth.AuthRoleMenu;
import com.wz.pilipili.user.mapper.AuthRoleMenuMapper;
import com.wz.pilipili.user.service.AuthRoleMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 权限控制--角色页面菜单关联表 服务实现类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-02
 */
@Service
public class AuthRoleMenuServiceImpl extends ServiceImpl<AuthRoleMenuMapper, AuthRoleMenu> implements AuthRoleMenuService {

    @Autowired
    private AuthRoleMenuMapper authRoleMenuMapper;

    @Override
    public List<AuthRoleMenu> getRoleMenuListByRoleIds(Set<Long> roleIds) {
        return authRoleMenuMapper.getRoleMenuListByRoleIds(roleIds);
    }
}
