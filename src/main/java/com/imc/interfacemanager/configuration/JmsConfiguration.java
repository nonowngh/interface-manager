package com.imc.interfacemanager.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.imc.interfacemanager.messaging.JmsReceiver;
import com.indigo.indigomq.IndigoMQConnectionFactory;
import com.indigo.indigomq.pool.PooledConnectionFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class JmsConfiguration {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    /**
     * 1. PooledConnectionFactory 직접 빈 등록
     * 외부 주입을 기다리지 않고 여기서 직접 초기화합니다.
     */
    @Bean(destroyMethod = "stop") // 종료 시 커넥션 풀을 안전하게 닫기 위함
    PooledConnectionFactory jmsConnectionFactory() {
        // 물리적 팩토리 설정
        IndigoMQConnectionFactory factory = new IndigoMQConnectionFactory();
        factory.setBrokerURL(brokerUrl);
        // 풀링 팩토리 설정
        PooledConnectionFactory pooledFactory = new PooledConnectionFactory();
        pooledFactory.setConnectionFactory(factory);
        // 풀링 세부 설정 (필요시 추가)
        pooledFactory.setMaxConnections(2); // 최대 물리 커넥션 수
        pooledFactory.setIdleTimeout(30000); // 유휴 타임아웃
        pooledFactory.setMaximumActive(500);
        log.info("✅ IndigoMQ PooledConnectionFactory 초기화 완료: {}", brokerUrl);
        return pooledFactory;
    }
    
    /**
     * 2. Message Listener Container 설정
     * 위에서 정의한 jmsConnectionFactory() 빈을 주입받습니다.
     */
    @Bean
    DefaultMessageListenerContainer customMessageListenerContainer(
            PooledConnectionFactory jmsConnectionFactory, 
            JmsReceiver jmsReceiver) {
        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        container.setConnectionFactory(jmsConnectionFactory);
        container.setDestinationName("IF.AD2IMC.Q");
        container.setMessageListener(jmsReceiver);
        container.setRecoveryInterval(5000);
        container.setConcurrency("3-10");
        container.setAcceptMessagesWhileStopping(false);
        container.setReceiveTimeout(500);
        
        container.setErrorHandler(t -> {
            log.error("⚠️ [JMS 연결 대기 중] 브로커가 응답하지 않습니다. (5초 후 재시도): {}", t.getMessage());
        });
        return container;
    }

    /**
     * 3. JmsTemplate 설정
     */
    @Bean(name = "interfaceJmsTemplate")
    JmsTemplate jmsTemplate(PooledConnectionFactory jmsConnectionFactory) {
        log.info("✅ interfaceJmsTemplate 빈 등록 완료");
        return new JmsTemplate(jmsConnectionFactory);
    }
}