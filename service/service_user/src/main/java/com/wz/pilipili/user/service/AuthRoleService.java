package com.wz.pilipili.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.auth.AuthRole;
import com.wz.pilipili.entity.auth.AuthRoleElementOperation;
import com.wz.pilipili.entity.auth.AuthRoleMenu;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 权限控制--角色表 服务类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-02
 */
public interface AuthRoleService extends IService<AuthRole> {

    List<AuthRoleElementOperation> getRoleElementOperationListByRoleIds(Set<Long> roleIds);

    List<AuthRoleMenu> getRoleMenuListByRoleIds(Set<Long> roleIds);

    AuthRole getRoleByCode(String code);
}
