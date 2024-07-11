package com.wz.pilipili.util;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RocketMQ消息发送工具类
 */
public class RocketMQUtil {

    /**
     * 同步发送消息
     *
     * @param producer
     * @param msg
     * @throws Exception
     */
    public static void syncSendMsg(DefaultMQProducer producer, Message msg) throws Exception {
        SendResult result = producer.send(msg);
        System.out.println(result);
    }

    /**
     * 异步发送消息
     *
     * @param producer
     * @param msg
     * @throws Exception
     */
    public static void asyncSendMsg(DefaultMQProducer producer, Message msg) throws Exception {
        producer.send(msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {//发送成功的回调
                Logger logger = LoggerFactory.getLogger(RocketMQUtil.class);
                logger.info("异步发送消息成功，消息id：" + sendResult.getMsgId());
            }

            @Override
            public void onException(Throwable e) {//发送失败的回调
                e.printStackTrace();
            }
        });
    }
}
