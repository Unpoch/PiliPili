package com.wz.pilipili.user.service.impl;

import com.wz.pilipili.entity.auth.AuthRoleElementOperation;
import com.wz.pilipili.user.mapper.AuthRoleElementOperationMapper;
import com.wz.pilipili.user.service.AuthRoleElementOperationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 权限控制--角色与元素操作关联表 服务实现类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-02
 */
@Service
public class AuthRoleElementOperationServiceImpl extends ServiceImpl<AuthRoleElementOperationMapper, AuthRoleElementOperation> implements AuthRoleElementOperationService {

    @Autowired
    private AuthRoleElementOperationMapper authRoleElementOperationMapper;

    @Override
    public List<AuthRoleElementOperation> getRoleElementOperationListByRoleIds(Set<Long> roleIds) {
        return authRoleElementOperationMapper.getRoleElementOperationListByRoleIds(roleIds);
    }
}
