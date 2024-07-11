package com.wz.pilipili.media;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.wz.pilipili.media.mapper")
@ComponentScan("com.wz.pilipili")
@EnableDiscoveryClient
public class ServiceMediaMainStarter {

    public static void main(String[] args) {
        SpringApplication.run(ServiceMediaMainStarter.class, args);
    }
}
