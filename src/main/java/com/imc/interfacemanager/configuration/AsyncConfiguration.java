package com.imc.interfacemanager.configuration;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync // 비동기 기능을 활성화합니다.
public class AsyncConfiguration {

    @Bean(name = "deployTaskExecutor") // 에러 메시지에 나온 바로 그 이름!
    Executor deployTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 서버 사양에 맞게 조정하세요.
        executor.setCorePoolSize(5);        // 기본 쓰레드 수
        executor.setMaxPoolSize(10);       // 최대 쓰레드 수
        executor.setQueueCapacity(100);    // 대기 큐 크기
        executor.setThreadNamePrefix("DeployTask-"); // 로그에서 식별하기 위함
        
        executor.initialize();
        return executor;
    }
}
