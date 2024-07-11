package com.wz.pilipili.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.auth.AuthRoleElementOperation;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 权限控制--角色与元素操作关联表 服务类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-02
 */
public interface AuthRoleElementOperationService extends IService<AuthRoleElementOperation> {

    List<AuthRoleElementOperation> getRoleElementOperationListByRoleIds(Set<Long> roleIds);
}
