package com.wz.pilipili.gateway.filter;

import com.wz.pilipili.constant.RedisConstant;
import com.wz.pilipili.constant.UserConstant;
import com.wz.pilipili.user.client.UserCoinFeignClient;
import com.wz.pilipili.user.client.UserInfoFeignClient;
import com.wz.pilipili.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;

/**
 * 首次登录过滤器
 */
@Component
public class FirstLoginFilter implements Ordered, GlobalFilter {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    @Autowired
    private UserCoinFeignClient userCoinFeignClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        //获取用户userId和角色信息
        String userId = request.getHeaders().getFirst("userId");
        String roles = request.getHeaders().getFirst("roles");
        assert userId != null;
        long uid = Long.parseLong(userId);
        //取出redis中的时间和当前时间做对比，如果不一致则是当日首次登录
        String lastLoginTime = redisTemplate.opsForValue().get(RedisConstant.LOGIN_TIME + userId);
        String now = DateUtil.dateFormat(new Date());
        //lastLoginTime为空说明第一次注册后登录 或者存在一天没有使用，对应的值被删除
        if (lastLoginTime == null || !DateUtil.dateCompareByDay(now, lastLoginTime)) {//或者最后登录时间不是当天，也说明是第一次登录
            increaseExperience(uid, UserConstant.FIVE_EXPERIENCE);
            increaseCoins(uid, UserConstant.ONE_COIN);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Async
    public void increaseExperience(Long userId, Integer experience) {
        userInfoFeignClient.increaseExperience(userId, experience);
    }

    @Async
    public void increaseCoins(Long userId, Integer coins) {
        userCoinFeignClient.updateUserCoin(userId, coins);
    }
}
