package com.wz.pilipili.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wz.pilipili.entity.auth.AuthRole;
import com.wz.pilipili.entity.auth.AuthRoleElementOperation;
import com.wz.pilipili.entity.auth.AuthRoleMenu;
import com.wz.pilipili.user.mapper.AuthRoleMapper;
import com.wz.pilipili.user.service.AuthRoleElementOperationService;
import com.wz.pilipili.user.service.AuthRoleMenuService;
import com.wz.pilipili.user.service.AuthRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 权限控制--角色表 服务实现类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-02
 */
@Service
public class AuthRoleServiceImpl extends ServiceImpl<AuthRoleMapper, AuthRole> implements AuthRoleService {

    @Autowired
    private AuthRoleElementOperationService authRoleElementOperationService;

    @Autowired
    private AuthRoleMenuService authRoleMenuService;

    /**
     * 根据角色id列表 获取 AuthRoleElementOperation（角色 页面操作元素权限）列表
     */
    @Override
    public List<AuthRoleElementOperation> getRoleElementOperationListByRoleIds(Set<Long> roleIds) {
        return authRoleElementOperationService.getRoleElementOperationListByRoleIds(roleIds);
    }

    /**
     * 根据角色id列表 获取 AuthRoleMenu（角色 页面菜单访问权限）列表
     */
    @Override
    public List<AuthRoleMenu> getRoleMenuListByRoleIds(Set<Long> roleIds) {
        return authRoleMenuService.getRoleMenuListByRoleIds(roleIds);
    }

    /**
     * 根据角色编码获取角色
     */
    @Override
    public AuthRole getRoleByCode(String code) {
        return this.getOne(new LambdaQueryWrapper<AuthRole>().eq(AuthRole::getCode, code));
    }
}
