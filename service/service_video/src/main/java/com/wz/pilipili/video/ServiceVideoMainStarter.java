package com.wz.pilipili.video;

import com.wz.pilipili.video.websocket.WebSocketService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("com.wz.pilipili")
@MapperScan("com.wz.pilipili.video")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.wz.pilipili")
@EnableAsync    //开启异步的功能
@EnableScheduling //开启定时任务
public class ServiceVideoMainStarter {

    public static void main(String[] args) {
        ApplicationContext app = SpringApplication.run(ServiceVideoMainStarter.class, args);
        WebSocketService.setApplicationContext(app);//设置上下文
    }
}
