package com.wz.pilipili.user.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootTest
public class IPTest {


    @Test
    public void testIP() throws UnknownHostException {
        InetAddress localHost = Inet4Address.getLocalHost();
        System.out.println("localHost = " + localHost);
    }
}
