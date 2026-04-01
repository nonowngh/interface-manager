package com.imc.interfacemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class InterfaceManagerApplication extends SpringBootServletInitializer { // 상속 추가
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(InterfaceManagerApplication.class);
    }
    public static void main(String[] args) {
        SpringApplication.run(InterfaceManagerApplication.class, args);
    }
}

