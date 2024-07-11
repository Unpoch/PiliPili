package com.wz.pilipili.context;

import com.wz.pilipili.entity.auth.UserRole;

import java.util.List;

/**
 * 用户/角色信息全局上下文
 */
public class UserContext {

    private static final ThreadLocal<Long> userThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<List<UserRole>> rolesThreadLocal = new ThreadLocal<>();

    public static void setCurUserId(Object id){
        userThreadLocal.set(Long.valueOf(id.toString()));
    }
    public static Long getCurUserId(){
        return userThreadLocal.get();
    }

    public static void setCurUserRoleList(List<UserRole> roleList) {
        rolesThreadLocal.set(roleList);
    }

    public static List<UserRole> getCurUserRoleList() {
        return rolesThreadLocal.get();
    }
    public static void clear(){
        userThreadLocal.remove();
        rolesThreadLocal.remove();
    }


}
