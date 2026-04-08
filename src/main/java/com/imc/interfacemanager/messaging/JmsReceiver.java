package com.imc.interfacemanager.messaging;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JmsReceiver implements MessageListener {

    private final ObjectMapper objectMapper;

    /**
     * DefaultMessageListenerContainer에 의해 호출되는 표준 메소드
     */
    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String text = ((TextMessage) message).getText();
                log.info("📩 JMS 메시지 수신: {}", text);

                // 1. JSON 문자열을 Map으로 변환
                Map<String, Object> payload = objectMapper.readValue(text, new TypeReference<Map<String, Object>>() {});
                
                // 2. 비즈니스 로직 처리 분리
                processPayload(payload);
            } else {
                log.warn("⚠️ 지원하지 않는 메시지 형식입니다: {}", message.getClass().getName());
            }
        } catch (JMSException e) {
            log.error("❌ JMS 메시지 읽기 오류: {}", e.getMessage());
        } catch (Exception e) {
            log.error("❌ 메시지 처리 중 비즈니스 로직 오류: {}", e.getMessage(), e);
        }
    }

    /**
     * 실제 비즈니스 로직을 수행하는 전용 메소드
     */
    private void processPayload(Map<String, Object> payload) {
        // 예: 배포 결과 코드(S/F) 및 인터페이스 ID 추출
        String interfaceId = (String) payload.get("interfaceId");
        String resultCode = (String) payload.get("resultCode"); // S 또는 F
        
        log.info("⚙️ 비즈니스 로직 수행 - Interface: {}, Result: {}", interfaceId, resultCode);

        // TODO: 여기서 Service를 호출하여 interface_info 테이블의 deploy_status 등을 업데이트하세요.
        // if ("S".equals(resultCode)) { ... }
    }
}