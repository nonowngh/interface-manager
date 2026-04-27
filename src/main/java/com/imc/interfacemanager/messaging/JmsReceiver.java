package com.imc.interfacemanager.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imc.interfacemanager.dto.DeployResultDto;
import com.imc.interfacemanager.service.DeployService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JmsReceiver implements MessageListener {

	private final ObjectMapper objectMapper;
	private final DeployService deployService;

	/**
	 * DefaultMessageListenerContainer에 의해 호출되는 표준 메소드
	 */
	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				String text = ((TextMessage) message).getText();
				log.info("📩 JMS 메시지 수신: {}", text);

				DeployResultDto result = objectMapper.readValue(text, DeployResultDto.class);
				processDeployResult(result);
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
	private void processDeployResult(DeployResultDto result) {
		log.info("⚙️ 배포 결과 업데이트 시작 - deploy-result : {}", result);

		// TODO: 배포 결과에 따른 DB 업데이트 로직 실행
		deployService.updateDeployResult(result);
	}
}