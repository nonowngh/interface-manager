package com.imc.interfacemanager.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 모든 경로(/**)에 대해 모든 도메인(*)에서의 요청을 허용합니다.
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        // 1. 확장자가 없는 경로 중, /api로 시작하지 않는 것만 index.html로 보냄
//        // 이 정규식은 'api'로 시작하지 않는 경로만 매핑합니다.
//    	registry.addViewController("/").setViewName("forward:/index.html");
//    }
}