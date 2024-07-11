package com.wz.pilipili.aspect;

import com.wz.pilipili.constant.AuthRoleConstant;
import com.wz.pilipili.context.UserContext;
import com.wz.pilipili.entity.auth.UserRole;
import com.wz.pilipili.entity.user.UserMoments;
import com.wz.pilipili.exception.ConditionException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据权限 切面类
 */
@Aspect
@Order(1) //优先级
@Component
public class DataLimitedAspect {

    /**
     * 切点：当注解@DataLimited被执行的时候 切入
     */
    @Pointcut("@annotation(com.wz.pilipili.annotation.DataLimited)")
    public void check() {
    }

    /**
     * 增强：
     * 在切点要执行的逻辑
     */
    @Before("check()")
    public void doBefore(JoinPoint joinPoint) {
        //1.获取用户相关的角色列表
        List<UserRole> userRoleList = UserContext.getCurUserRoleList();
        //2.获取用户角色的唯一编码
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        //3.获取切点的参数
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof UserMoments) {//如果是UserMoments类型，根据type类型进行数据权限控制
                UserMoments userMoments = (UserMoments) arg;
                String type = userMoments.getType();
                if(roleCodeSet.contains(AuthRoleConstant.ROLE_LV0) && !"0".equals(type)) {
                    throw new ConditionException("参数异常！");
                }
            }
        }
    }


}
