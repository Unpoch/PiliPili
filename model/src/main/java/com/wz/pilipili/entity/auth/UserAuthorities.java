package com.wz.pilipili.entity.auth;

import lombok.Data;

import java.util.List;

/**
 * 用户权限
 */
@Data
public class UserAuthorities {

    //页面元素 操作权限列表
    List<AuthRoleElementOperation> roleElementOperationList;

    //页面菜单（访问），操作权限列表
    List<AuthRoleMenu> roleMenuList;

}
