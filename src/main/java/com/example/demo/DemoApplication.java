package com.example.demo;

import com.example.demo.netty.NettyServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetSocketAddress;
@Slf4j
@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Autowired
    NettyServer nettyServer;

    @Override
    public void run(String... args) throws Exception {
        InetSocketAddress address = new InetSocketAddress(DefaultData.SOCKET_IP,DefaultData.SOCKET_PORT);
        log.info("netty服务启动,地址:" + DefaultData.SOCKET_IP);
        nettyServer.start(address);
    }
}
