package com.wz.pilipili.video.websocket;

import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.util.StringUtils;
import com.wz.pilipili.constant.MQConstant;
import com.wz.pilipili.context.UserContext;
import com.wz.pilipili.entity.video.Danmu;
import com.wz.pilipili.util.RocketMQUtil;
import com.wz.pilipili.video.service.DanmuService;
import lombok.Getter;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WebSocket服务类
 */
@Component
@ServerEndpoint("/wsServer") //相当于一个访问路径的作用
@Getter
public class WebSocketService {

    //日志
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //当前长连接的人数（在线人数）, 使用AtomicInteger，原子操作类，保证线程安全
    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);

    //并发安全集合，用来保存每一个客户端对应的WebSocketService类对象
    /*
    SpringBoot依赖注入的方式默认是单例注入的，但是我们多个客户端要对应多个长连接，也就是多个WebSocketService
    因此我们需要设计成多例的，那么就需要这个集合类，保存每一个客户端对应的WebSocketService
     */
    public static final ConcurrentHashMap<String, WebSocketService> WEBSOCKET_CONCUR_MAP = new ConcurrentHashMap<>();

    //会话，当一个客户端和服务端保持长连接成功了，就生成一个对应的会话。下一次客户端和服务端进行交互时，就可以通过Session进行通信
    private Session session;

    private String sessionId;//唯一标识（也可以相当于客户端的唯一标识）

    /*
    显然WebSocketService采用的是多例模式，因为SpringBoot默认采用单例模式注入bean
    因此我们在该Service@Autowire注入bean，只会注入一次
    其他的WebSocketService实例，就没有@Autowire注入的这个bean了
    例如我们
    @Autowire
    private RedisTemplate redisTemplate  ,这个redisTemplate只会存在与某一个WebSocketService实例
    其他实例下redisTemplate 是 null

    为了在WebSocketService中使用多例模式，我们可以使用Spring的上下文对象ApplicationContext
    但是我们不能直接
    @Autowire注入，这样ApplicationContext对象还是单例的
    为了让所有WebSocketService实例共享一个ApplicationContext对象，我们将属性设置成static即可
    并在主启动类启动的时候，为这个属性赋值

    这样我们就可以实现多例模式，例如给每一个WebSocketService实例配置RedisTemplate对象（这里是通过bean名称获取bean）
    RedisTemplate<String,String> redisTemplate = (RedisTemplate<String, String>) WebSocketService.APPLICATION_CONTEXT.getBean("redisTemplate");
     */
    private static ApplicationContext APPLICATION_CONTEXT;

    private Long userId;//关联的userId

    // @Autowired
    // private DanmuService danmuService;  WebSocketService是多例模式，不能直接注入


    //属性赋值
    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebSocketService.APPLICATION_CONTEXT = applicationContext;
    }

    /**
     * 开启/建立连接
     */
    @OnOpen   //当连接成功时，调用该注解标注的方法
    public void openConnection(Session session) {
        //1.sessionId属性赋值  session属性赋值 userId属性赋值
        this.userId = UserContext.getCurUserId();
        this.sessionId = session.getId();
        this.session = session;
        //2.判断map中是否存在sessionId这个key，存在则删除，然后加入sessionId : session
        //3.如果不存在这个key，说明是第一次连接，那么map记录，在线人数 + 1
        if (WEBSOCKET_CONCUR_MAP.containsKey(sessionId)) {
            WEBSOCKET_CONCUR_MAP.remove(sessionId);
            WEBSOCKET_CONCUR_MAP.put(sessionId, this);
        } else {
            WEBSOCKET_CONCUR_MAP.put(sessionId, this);
            ONLINE_COUNT.getAndIncrement();//在线人数 + 1
        }
        //4.如果连接成功，要给前端发送消息表明连接成功，这里用状态码 "0"表示
        try {
            this.sendMsg("0");
            logger.info("用户连接成功：{}，当前在线人数为：{}", sessionId, ONLINE_COUNT.get());
        } catch (Exception e) {
            logger.info("连接失败！");
        }
    }


    /**
     * 关闭连接
     * 1.服务端断开了
     * 2.客户端页面刷新，或客户端关闭页面
     */
    @OnClose
    public void closeConnection() {
        //直接去查询本地的session是否存在（调用到该方法时，属性已经赋值了）
        if (WEBSOCKET_CONCUR_MAP.containsKey(sessionId)) {
            WEBSOCKET_CONCUR_MAP.remove(sessionId);//移除会话
            ONLINE_COUNT.getAndDecrement();//在线人数 - 1
        }
        logger.info("用户退出：{}，当前在线人数：{}", sessionId, ONLINE_COUNT.get());
    }


    /**
     * 客户端发送弹幕
     * 给服务端传输该弹幕消息
     */
    @OnMessage
    public void onMessage(String message) {
        logger.info("用户信息：{}，报文内容：{}", sessionId, message);
        try {
            //1.群发消息：拿到弹幕后，群发给所有正在连接的客户端
            // 1）具体来说就是遍历map，拿到所有客户端对应WebSocketService，
            // 2）然后获取MQ的生产者，构建生产到MQ的消息，生产到MQ中（削峰）
            // 3）MQ的消费者消费消息后，消费的逻辑就是 使用WebSocketService发送弹幕消息到前端
            if (!StringUtils.isNullOrEmpty(message)) {
                for (Map.Entry<String, WebSocketService> entry : WEBSOCKET_CONCUR_MAP.entrySet()) {
                    // WebSocketService webSocketService = entry.getValue();
                    DefaultMQProducer danmusProducer = (DefaultMQProducer) APPLICATION_CONTEXT.getBean("danmusProducer");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("sessionId", sessionId);
                    jsonObject.put("message", message);
                    Message msg = new Message(MQConstant.TOPIC_DANMUS, jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8));
                    RocketMQUtil.asyncSendMsg(danmusProducer, msg);
                }
            }
            //2.将message解析为Danmu对象
            //  Danmu对象要设置userId，但是观看视频的也可能是游客（userId == null）
            //3.弹幕保存到redis中
            //4.弹幕异步保存到数据库中，调用DanmuService的异步保存到数据库的方法（这里只做到异步，削峰具体需要引入MQ）
            if (this.userId != null) {
                Danmu danmu = JSONObject.parseObject(message, Danmu.class);
                danmu.setUserId(userId);
                //获取danmuService
                DanmuService danmuService = (DanmuService) APPLICATION_CONTEXT.getBean("danmuService");
                danmuService.addDanmuToRedis(danmu);
                danmuService.asyncAddDanmu(danmu);
            }
        } catch (Exception e) {
            logger.info("弹幕接收出错！");
            e.printStackTrace();
        }

    }

    /**
     * 发生错误时
     */
    @OnError
    public void onError(Throwable error) {
    }

    //TODO

    /**
     * 定时任务
     * 定时获取视频的在线任务推送给前端
     */
    @Scheduled(fixedRate = 5000) //每5s
    private void getOnlineCount() throws IOException{
        //遍历map，获取所有和服务端通过WebSocket连接的客户端的会话取出来
        //然后使用WebSocketService推送消息即可
        for (Map.Entry<String, WebSocketService> entry : WEBSOCKET_CONCUR_MAP.entrySet()) {
            WebSocketService webSocketService = entry.getValue();
            if (webSocketService.getSession().isOpen()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("onlineCount", ONLINE_COUNT.get());
                jsonObject.put("msg","当前在线人数为" + ONLINE_COUNT.get());
                webSocketService.sendMsg(jsonObject.toJSONString());
            }
        }
    }


    /**
     * 发送文本消息给前端
     */
    public void sendMsg(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

}
