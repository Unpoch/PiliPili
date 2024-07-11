package com.wz.pilipili.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.auth.AuthRoleMenu;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 权限控制--角色页面菜单关联表 服务类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-02
 */
public interface AuthRoleMenuService extends IService<AuthRoleMenu> {

    List<AuthRoleMenu> getRoleMenuListByRoleIds(Set<Long> roleIds);
}
