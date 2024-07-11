package com.wz.pilipili.video.mq;

import com.alibaba.fastjson.JSONObject;
import com.wz.pilipili.constant.MQConstant;
import com.wz.pilipili.video.websocket.WebSocketService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * RocketMQ配置类
 */
@Configuration
public class RocketMQConfig {

    @Value("rocketmq.name.server.address")
    private String nameServerAddr;

    /**
     * 生产者
     */
    @Bean("danmusProducer")
    public DefaultMQProducer momentsProducer() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer(MQConstant.GROUP_DANMUS);
        producer.setNamesrvAddr(nameServerAddr);
        producer.start();
        return producer;
    }


    /**
     * 消费者
     */
    @Bean("danmusConsumer")
    public DefaultMQPushConsumer momentsConsumer() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(MQConstant.GROUP_DANMUS);
        consumer.setNamesrvAddr(nameServerAddr);
        consumer.subscribe(MQConstant.TOPIC_DANMUS, "*");//订阅生产者
        //给消费者添加监听器:并行处理的监听器
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                //1.首先获取消息，将JSON格式的消息进行解析
                //  获取sessionId 和 弹幕内容
                MessageExt msg = msgs.get(0);
                byte[] msgByte = msg.getBody();
                String bodyStr = new String(msgByte);
                JSONObject jsonObject = JSONObject.parseObject(bodyStr);
                String sessionId = jsonObject.getString("sessionId");
                String message = jsonObject.getString("message");
                //2.然后通过sessionId获取对应的WebSocketService
                // 调用webSocketService发送消息的方法推送弹幕到前端
                WebSocketService webSocketService = WebSocketService.WEBSOCKET_CONCUR_MAP.get(sessionId);
                if(webSocketService.getSession().isOpen()) {//如果会话是打开的
                    try {
                        webSocketService.sendMsg(message);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                //3.返回消息被成功消费
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }

        });
        consumer.start();//启动消费者实例
        return consumer;
    }

}
