package com.wz.pilipili.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wz.pilipili.entity.auth.AuthRoleElementOperation;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 权限控制--角色与元素操作关联表 Mapper 接口
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-02
 */
public interface AuthRoleElementOperationMapper extends BaseMapper<AuthRoleElementOperation> {

    List<AuthRoleElementOperation> getRoleElementOperationListByRoleIds(@Param("roleIds") Set<Long> roleIds);
}
