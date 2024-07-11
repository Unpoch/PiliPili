package com.wz.pilipili.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(basePackages = "com.wz.pilipili")
@MapperScan("com.wz.pilipili.user.mapper")
@EnableDiscoveryClient //Nacos服务发现
@EnableFeignClients(basePackages = "com.wz.pilipili")
public class ServiceUserMainStarter {

    public static void main(String[] args) {
        SpringApplication.run(ServiceUserMainStarter.class, args);
    }
}
