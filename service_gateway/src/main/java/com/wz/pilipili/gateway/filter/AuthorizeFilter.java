package com.wz.pilipili.gateway.filter;

import com.wz.pilipili.exception.ConditionException;
import com.wz.pilipili.gateway.config.WhiteListConfig;
import com.wz.pilipili.util.TokenUtil;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * 过滤器，验证token，设置请求头
 */
@Component
public class AuthorizeFilter implements Ordered, GlobalFilter {

    @Autowired
    private WhiteListConfig whiteList;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取request和response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //2.判断是否是登录 / 登录-双令牌 / 刷新令牌
        String path = request.getURI().getPath();
        if (whiteList.isWhiteListed(path)) {
            return chain.filter(exchange);//放行
        }
        //3.获取token
        String token = request.getHeaders().getFirst("token");
        //4.判断token是否存在
        if (token == null || token.isEmpty()) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //4.判断token是否有效
        Long userId = TokenUtil.verifyToken(token);
        String rolesJson = TokenUtil.getRolesFromToken(token);
        if (userId < 0 || StringUtil.isNullOrEmpty(rolesJson)) {
            throw new ConditionException("非法用户！");
        }
        //5.把解析后的用户信息存储到header
        ServerHttpRequest serverHttpRequest = request.mutate().headers(httpHeaders -> {
            httpHeaders.add("userId", userId.toString());
            httpHeaders.add("roles", rolesJson);
        }).build();
        //重置header
        exchange.mutate().request(serverHttpRequest).build();
        //6.放行
        return chain.filter(exchange);
    }

    /**
     * 优先级设置  值越小  优先级越高
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}

