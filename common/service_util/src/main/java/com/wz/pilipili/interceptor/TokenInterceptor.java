package com.wz.pilipili.interceptor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wz.pilipili.context.UserContext;
import com.wz.pilipili.entity.auth.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

/**
 * token拦截器，为了方便请求设置或者获取userId（不要放在网关模块）
 */
@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        String rolesJson = request.getHeader("roles");
        if (userId != null && rolesJson != null) {
            Long id = Long.valueOf(userId);
            List<UserRole> roleList = JSONArray.parseArray(rolesJson, UserRole.class);
            UserContext.setCurUserId(id);
            UserContext.setCurUserRoleList(roleList);
            log.info("设置用户信息到ThreadLocal中:userId = {},roleList={}", userId,roleList);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("清理threadLocal");
        //一定要清理，要不然可能会发生内存泄漏
        UserContext.clear();
    }
}

