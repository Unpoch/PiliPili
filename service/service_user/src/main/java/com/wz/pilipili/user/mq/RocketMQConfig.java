package com.wz.pilipili.user.mq;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wz.pilipili.constant.RedisConstant;
import com.wz.pilipili.constant.MQConstant;
import com.wz.pilipili.entity.user.UserFollowing;
import com.wz.pilipili.entity.user.UserMoments;
import com.wz.pilipili.user.service.UserFollowingService;
import io.netty.util.internal.StringUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * RocketMQ配置类
 * 业务上配合Redis进行使用
 */
@Configuration
public class RocketMQConfig {

    @Value("${rocketmq.name.server.address}")
    private String nameServerAddr;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserFollowingService userFollowingService;

    /**
     * 动态消息 生产者
     */
    @Bean("momentsProducer")
    public DefaultMQProducer momentsProducer() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer(MQConstant.GROUP_MOMENTS);
        producer.setNamesrvAddr(nameServerAddr);
        producer.start();
        return producer;
    }

    /**
     * 动态消息 消费者
     * 这里使用的是推送的方式，推送消息（DefaultMQPushConsumer）
     */
    @Bean("momentsConsumer")
    public DefaultMQPushConsumer momentsConsumer() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(MQConstant.GROUP_MOMENTS);
        consumer.setNamesrvAddr(nameServerAddr);
        consumer.subscribe(MQConstant.TOPIC_MOMENTS, "*");//订阅生产者
        //给消费者添加监听器:并行处理的监听器
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                //1.因为生产者只生产了一条消息，因此get(0)即可
                MessageExt msg = msgs.get(0);
                if (msg == null) {
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                //2.获取消息体内容,msg.getBody()得到的是字节数组
                //  获取用户发送的动态：实体类UserMoments
                String bodyStr = new String(msg.getBody());
                UserMoments userMoments = JSONObject.toJavaObject(JSONObject.parseObject(bodyStr), UserMoments.class);
                //3. 获取订阅了该用户动态的粉丝列表
                //  对于每一个fan，先去redis查出他的动态列表（粉丝肯定也订阅了其他用户，其他用户发布的动态一样会放到这个列表中）
                //  然后将该用户的动态添加到 fan的动态列表中，并添加会redis
                //  具体推送动态即：将动态存储到redis中，粉丝去redis中查询（拉取pull）
                Long userId = userMoments.getUserId();
                List<UserFollowing> fanList = userFollowingService.getFanList(userId);
                for (UserFollowing fan : fanList) {
                    String key = RedisConstant.SUBSCRIBE_MOMENTS + fan.getUserId();
                    // redisTemplate.opsForValue().set(key, "1");
                    String fanSubscribeListStr = redisTemplate.opsForValue().get(key);//存入的是JSON格式的字符串
                    List<UserMoments> fanSubscribeList;
                    if (StringUtil.isNullOrEmpty(fanSubscribeListStr)) {
                        fanSubscribeList = new ArrayList<>();
                    } else {
                        //将JSON格式的字符串转化为List<UserMoments>类型
                        fanSubscribeList = JSONArray.parseArray(fanSubscribeListStr, UserMoments.class);
                    }
                    fanSubscribeList.add(userMoments);//添加到fan的动态列表中
                    redisTemplate.opsForValue().set(key, JSONObject.toJSONString(fanSubscribeList));//再添加到redis中
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        return consumer;
    }

}
