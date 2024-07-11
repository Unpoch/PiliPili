package com.wz.pilipili.user.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wz.pilipili.constant.RedisConstant;
import com.wz.pilipili.constant.MQConstant;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.user.UserInfo;
import com.wz.pilipili.entity.user.UserMoments;
import com.wz.pilipili.user.mapper.UserMomentsMapper;
import com.wz.pilipili.user.service.UserInfoService;
import com.wz.pilipili.user.service.UserMomentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wz.pilipili.util.RocketMQUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户动态表 服务实现类
 *
 * @author unkonwnzz
 * @since 2024-05-31
 */
@Service
public class UserMomentsServiceImpl extends ServiceImpl<UserMomentsMapper, UserMoments> implements UserMomentsService {

    //注入SpringBoot应用上下文对象，可以获取所有跟SpringBoot相关的配置
    @Autowired
    private ApplicationContext app;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 用户发布动态
     */
    @Override
    public void pushMoments(UserMoments userMoments) throws Exception {
        //1.数据库中插入UserMoments
        baseMapper.insert(userMoments);
        //2.使用MQ发送消息，通知该用户的粉丝 其发布了一条新动态
        // 使用ApplicationContext获取生产者
        DefaultMQProducer momentsProducer = (DefaultMQProducer) app.getBean("momentsProducer");
        //3.创建Message
        Message message = new Message(MQConstant.TOPIC_MOMENTS,
                JSONObject.toJSONString(userMoments).getBytes(StandardCharsets.UTF_8));
        //4.调用RocketMQUtil中的发送消息方法
        RocketMQUtil.syncSendMsg(momentsProducer, message);
    }

    /**
     * 根据userId查询用户订阅的动态列表
     */
    @Override
    public List<UserMoments> getUserSubscribedMoments(Long userId) {
        //从redis中查询当前用户的动态列表
        String key = RedisConstant.SUBSCRIBE_MOMENTS + userId;
        String userSubMomentsListStr = redisTemplate.opsForValue().get(key);
        return JSONArray.parseArray(userSubMomentsListStr, UserMoments.class);
    }

    /**
     * 分页查询用户动态
     */
    @Override
    public PageResult<UserMoments> pageListUserMoments(Integer pageNo, Integer pageSize, Long userId) {
        //1.封装分页参数
        int start = (pageNo - 1) * pageSize;
        Page<UserMoments> pageParams = new Page<>(start, pageSize);
        //2.分页查询用户动态
        IPage<UserMoments> page = baseMapper.selectPage(pageParams, new LambdaQueryWrapper<UserMoments>()
                .eq(UserMoments::getUserId, userId)
                .orderByDesc(UserMoments::getId));
        List<UserMoments> userMomentsList = page.getRecords();
        //3.封装UserMoments的content字段和UserInfo字段
        //TODO : 根据type区分Content，查询对应表
        UserInfo userInfo = userInfoService.getUserInfoByUserId(userId);
        userMomentsList.forEach(userMoments -> {
            // userMoments.setContent(xx);
            userMoments.setUserInfo(userInfo);
        });
        //4.封装PageResult对象
        return new PageResult<>(page.getTotal(), userMomentsList);
    }
}
