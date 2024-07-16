package com.wz.pilipili.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(basePackages = "com.wz.pilipili")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.wz.pilipili")
@EnableAsync
public class ServiceGatewayMainStarter {

    public static void main(String[] args) {
        SpringApplication.run(ServiceGatewayMainStarter.class, args);
    }
}
