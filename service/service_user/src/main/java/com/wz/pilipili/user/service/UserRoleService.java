package com.wz.pilipili.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.auth.UserRole;

import java.util.List;

/**
 * <p>
 * 用户角色关联表 服务类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-02
 */
public interface UserRoleService extends IService<UserRole> {

    List<UserRole> getUserRoleListByUserId(Long userId);

    void addUserRole(UserRole userRole);

    String getMaxRoleCodeByUserId(Long userId);
}
