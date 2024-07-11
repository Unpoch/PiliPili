package com.wz.pilipili.aspect;

import com.wz.pilipili.annotation.ApiLimitedRole;
import com.wz.pilipili.context.UserContext;
import com.wz.pilipili.entity.auth.UserRole;
import com.wz.pilipili.exception.ConditionException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 接口权限 切面类
 */
@Aspect
@Order(1) //优先级
@Component
public class ApiLimitedRoleAspect {

    /**
     * 切点：当注解@ApiLimitedRole被执行的时候 切入
     */
    @Pointcut("@annotation(com.wz.pilipili.annotation.ApiLimitedRole)")
    public void check() {
    }

    /**
     * 增强：
     * 在切点要执行的逻辑
     */
    @Before("check() && @annotation(apiLimitedRole)")
    public void doBefore(JoinPoint joinPoint, ApiLimitedRole apiLimitedRole) {
        //1.获取用户相关的角色列表
        List<UserRole> userRoleList = UserContext.getCurUserRoleList();
        //2.获取注解的参数：哪些角色被限制（code）
        String[] limitedRoleCodeList = apiLimitedRole.limitedRoleCodeList();
        //3.两个集合取交集，如果交集有元素，说明用户的角色在被限制的角色列表中，权限不足
        Set<String> limitedRoleCodeSet = Arrays.stream(limitedRoleCodeList).collect(Collectors.toSet());
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        roleCodeSet.retainAll(limitedRoleCodeSet);//取交集
        if(!roleCodeSet.isEmpty()) {
            throw new ConditionException("权限不足！");
        }
    }


}
