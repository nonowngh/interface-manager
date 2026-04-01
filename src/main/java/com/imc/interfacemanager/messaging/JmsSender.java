package com.imc.interfacemanager.messaging;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JmsSender {

    @Async("deployTaskExecutor") // 별도 스레드 풀 사용 권장
    public void sendDeployMessages(String interfaceId, List<String> adapterIds) {
        try {
            // 실제 JMS 발송 로직 (jmsTemplate.convertAndSend...)
            log.info("JMS 전송 시작: {}", adapterIds);
        } catch (Exception e) {
            log.error("JMS 전송 중 에러 발생: ", e);
            // 필요 시 여기서 DB 상태를 '실패'로 업데이트하는 로직 추가
        }
    }
}