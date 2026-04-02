package com.imc.interfacemanager.messaging;

import java.util.Map;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JmsReceiver {
	/**
	 * destination: 큐 또는 토픽 이름 containerFactory: 위 Config에서 정의한 빈 이름
	 */
	@JmsListener(destination = "${jms.receiver.destination:IF.AD2IMC.Q}", containerFactory = "jmsListenerContainerFactory")
	public void receiveMessage(Map<String, Object> message) {
		try {
			log.info("📩 JMS 메시지 수신: {}", message);

			// 전송받은 데이터 추출 (예: data 필드)
			Object data = message.get("data");
			log.info("처리할 데이터: {}", data);

			// TODO: 비즈니스 로직 수행 (예: PicsService 호출)

		} catch (Exception e) {
			log.error("❌ JMS 메시지 처리 중 오류 발생: {}", e.getMessage(), e);
			// 에러 발생 시 브로커 설정에 따라 DLQ(Dead Letter Queue)로 이동하거나 재시도됨
		}
	}
}
