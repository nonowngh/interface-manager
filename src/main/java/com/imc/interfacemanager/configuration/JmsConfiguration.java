package com.imc.interfacemanager.configuration;

import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

// IndigoMQ 전용 팩토리 임포트
import com.indigo.indigomq.IndigoMQConnectionFactory;

@Configuration
@EnableJms
public class JmsConfiguration {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Bean
    ConnectionFactory connectionFactory() {
        IndigoMQConnectionFactory factory = new IndigoMQConnectionFactory();
        factory.setBrokerURL(brokerUrl);
        return factory;
    }

    @Bean
    DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        
        // 재연결 및 타임아웃 설정 (종료 시 반응성 향상)
        factory.setRecoveryInterval(5000L); 
        factory.setReceiveTimeout(1000L);
        
        return factory;
    }
    
    @Bean
    JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        // IndigoMQ와의 세션 처리 방식을 설정 (기본값 사용 가능)
        jmsTemplate.setSessionAcknowledgeMode(javax.jms.Session.AUTO_ACKNOWLEDGE);
        return jmsTemplate;
    }
}