package com.wz.pilipili.user.service.impl;

import com.wz.pilipili.constant.AuthRoleConstant;
import com.wz.pilipili.entity.auth.*;
import com.wz.pilipili.user.service.AuthRoleService;
import com.wz.pilipili.user.service.UserAuthService;
import com.wz.pilipili.user.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户权限 服务类
 */
@Service
public class UserAuthServiceImpl implements UserAuthService {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private AuthRoleService authRoleService;


    /**
     * 根据userId获取用户权限
     */
    @Override
    public UserAuthorities getUserAuthorities(Long userId) {
        //1.根据userId查询用户关联的角色列表
        List<UserRole> userRoleList = userRoleService.getUserRoleListByUserId(userId);
        Set<Long> roleIds = userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        //2.根据角色id列表查询权限列表
        //2.1 查询页面元素操作权限列表
        //2.2 查询页面访问操作权限列表
        List<AuthRoleElementOperation> roleElementOperationList = authRoleService.getRoleElementOperationListByRoleIds(roleIds);
        List<AuthRoleMenu> roleMenuList = authRoleService.getRoleMenuListByRoleIds(roleIds);
        //3.根据权限列表构建UserAuthorities对象返回
        UserAuthorities userAuthorities = new UserAuthorities();
        userAuthorities.setRoleElementOperationList(roleElementOperationList);
        userAuthorities.setRoleMenuList(roleMenuList);
        return userAuthorities;
    }

    /**
     * 添加用户默认权限角色
     */
    @Override
    public void addUserDefaultRole(Long userId) {
        UserRole userRole = new UserRole();
        //1.根据角色编码 获取角色
        AuthRole role = authRoleService.getRoleByCode(AuthRoleConstant.ROLE_LV0);
        userRole.setUserId(userId);
        userRole.setRoleId(role.getId());
        //2.添加到 用户角色表中
        userRoleService.addUserRole(userRole);
    }
}
