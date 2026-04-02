package com.imc.interfacemanager.messaging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.DeliveryMode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor // JmsTemplate 자동 주입을 위함
public class JmsSender {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${jms.sender.destination:IF.IMC2AD.Q}") // yml에 설정한 큐 이름
    private String destination;

    /**
     * 비동기로 배포 메시지를 IndigoMQ로 전송합니다.
     */
    @Async("deployTaskExecutor")
    public void sendDeployMessages(String interfaceId, List<String> adapterIds) {
    	if (adapterIds == null || adapterIds.isEmpty()) {
            log.warn("⚠️ 전송할 어댑터 ID 리스트가 비어있습니다. (InterfaceId: {})", interfaceId);
            return;
        }

        log.info("🚀 JMS 전송 시작 - Interface: {}, Total Adapters: {}", interfaceId, adapterIds.size());

        try {
            // 리스트 개수만큼 반복 실행
            for (String adapterId : adapterIds) {
                
                // 1. 바디 구성 (JSON 문자열로 변환)
                Map<String, Object> messagePayload = new HashMap<>();
                messagePayload.put("interfaceId", interfaceId);
                messagePayload.put("adapterId", adapterId); // 개별 어댑터 ID 포함
                messagePayload.put("deployData", "");
                
                String jsonPayload = objectMapper.writeValueAsString(messagePayload);

                // 2. 메시지 전송
                jmsTemplate.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                jmsTemplate.convertAndSend(destination, jsonPayload, message -> {
                    // 헤더(Property) 설정
                    message.setStringProperty("deployType", "each");
                    message.setStringProperty("adaptorId", adapterId); // 헤더에도 개별 ID 설정
                    return message;
                });

                log.info("[전송성공] Adapter: {}", adapterId);
            }

            log.info("✅ 모든 JMS 전송 완료 (Interface: {})", interfaceId);

        } catch (Exception e) {
            log.error("❌ JMS 전송 중 에러 발생 (InterfaceId: {}): ", interfaceId, e);
            // 비즈니스 로직에 따른 후속 처리 (DB 업데이트 등)
        }
    }
}