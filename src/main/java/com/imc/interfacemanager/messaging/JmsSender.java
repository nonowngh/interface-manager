package com.imc.interfacemanager.messaging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JmsSender {

	private final JmsTemplate jmsTemplate;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Value("${jms.sender.destination:IF.IMC2AD.Q}") // yml에 설정한 큐 이름
	private String destination;

	/**
	 * 비동기로 배포 메시지를 IndigoMQ로 전송합니다.
	 * 
	 * @throws Exception
	 */
	@Async("deployTaskExecutor")
	public void sendDeployMessages(String interfaceId, List<String> adapterIds) throws Exception {
		if (adapterIds == null || adapterIds.isEmpty()) {
			log.warn("⚠️ 전송할 어댑터 ID 리스트가 비어있습니다. (InterfaceId: {})", interfaceId);
			return;
		}

		log.info("🚀 JMS 전송 시작 - Interface: {}, Total Adapters: {}", interfaceId, adapterIds.size());

		try {
			for (String adapterId : adapterIds) {
				// 1. 전송 시도 로그를 더 명확히 찍어 어디서 멈추는지 확인
				log.info(">>>> [전송시도] Adapter: {} (Destination: {})", adapterId, destination);

				Map<String, Object> messagePayload = new HashMap<>();
				messagePayload.put("interfaceId", interfaceId);
				messagePayload.put("adapterId", adapterId);
				messagePayload.put("deployData", "");

				String jsonPayload = objectMapper.writeValueAsString(messagePayload);

				// 2. 전송 로직 (예외 발생 시 catch 블록으로 가는지 확인)
				jmsTemplate.convertAndSend(destination, jsonPayload, message -> {
					message.setStringProperty("deployType", "each");
					message.setStringProperty("adaptorId", adapterId);
					return message;
				});

				log.info("<<<< [전송성공] Adapter: {}", adapterId);
			}
		} catch (Exception e) {
			log.error("❌ JMS 전송 처리 중 예외 발생: {}", e.getMessage(), e);
			throw e;
		}
	}

}