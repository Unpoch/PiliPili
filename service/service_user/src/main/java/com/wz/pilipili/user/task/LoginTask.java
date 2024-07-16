package com.wz.pilipili.user.task;

import com.mysql.cj.util.StringUtils;
import com.wz.pilipili.constant.RedisConstant;
import com.wz.pilipili.entity.user.UserInfo;
import com.wz.pilipili.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 处理登录逻辑的定时任务类
 */
@Component
public class LoginTask {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserInfoService userInfoService;

    //每天凌晨五点同步所有用户的最后登录时间
    @Scheduled(cron = "0 0 5 * * ?")
    public void syncLastLoginDate() throws ParseException {
        //获取所有key
        Set<String> keys = redisTemplate.keys(RedisConstant.LOGIN_TIME + "*");
        if (keys != null && !keys.isEmpty()) {
            Map<Long, String> userLastLoginDateMap = new HashMap<>();
            for (String key : keys) {
                String uid = key.split(":")[1];
                long userId = Long.parseLong(uid);
                String lastLoginDateStr = redisTemplate.opsForValue().get(key);
                if (!StringUtils.isNullOrEmpty(lastLoginDateStr)) {
                    userLastLoginDateMap.put(userId, lastLoginDateStr);
                }
            }
            userInfoService.updateUserLastLoginDate(userLastLoginDateMap);
        }
    }

}
