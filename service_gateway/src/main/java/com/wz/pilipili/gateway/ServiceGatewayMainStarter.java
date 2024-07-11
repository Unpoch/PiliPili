package com.wz.pilipili.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
// @ComponentScan(basePackages = "com.wz")
// @EnableDiscoveryClient
public class ServiceGatewayMainStarter {

    public static void main(String[] args) {
        SpringApplication.run(ServiceGatewayMainStarter.class, args);
    }
}
