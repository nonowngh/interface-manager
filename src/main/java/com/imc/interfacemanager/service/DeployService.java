package com.imc.interfacemanager.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imc.interfacemanager.dto.AdaptorInfoDto;
import com.imc.interfacemanager.entity.DeployAdaptorMapping;
import com.imc.interfacemanager.entity.DeployHistory;
import com.imc.interfacemanager.messaging.JmsSender;
import com.imc.interfacemanager.repository.DeployAdaptorMappingRepository;
import com.imc.interfacemanager.repository.DeployAdaptorRepository;
import com.imc.interfacemanager.repository.DeployHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeployService {

	private final DeployAdaptorMappingRepository deployAdaptorMappingRepository;
	private final DeployAdaptorRepository deployAdaptorRepository;
	private final DeployHistoryRepository deployHistoryRepository;
	private final JmsSender jmsSender;
	private final ObjectMapper objectMapper; // JSON 변환을 위해 주입

	/**
	 * 전체 어댑터 상태 및 매핑 정보 조회
	 */
	public List<AdaptorInfoDto> getFullAdaptorStatus(String interfaceId) {
		return deployAdaptorRepository.findAllWithMappingStatus(interfaceId);
	}

	@Transactional
	public void processAsyncDeploy(String interfaceId, List<String> adapterIds) {
	    
	    // [Step 1] 매핑 정보 동기화 (기존 삭제 후 일괄 저장)
	    deployAdaptorMappingRepository.deleteByInterfaceId(interfaceId);
	    
	    // 리스트를 엔티티 리스트로 변환
	    List<DeployAdaptorMapping> mappings = adapterIds.stream()
	        .map(adapterId -> {
	            DeployAdaptorMapping mapping = new DeployAdaptorMapping();
	            mapping.setInterfaceId(interfaceId);
	            mapping.setAdapterId(adapterId);
	            mapping.setCreatedBy("admin");
	            return mapping;
	        }).collect(Collectors.toList());
	    
	    // 한 번에 저장 (Batch Insert 효과)
	    deployAdaptorMappingRepository.saveAll(mappings);
	    
	    log.info("인터페이스 [{}]에 어댑터 {}개 매핑 완료", interfaceId, adapterIds.size());

	    createDeployHistory(interfaceId, adapterIds);

	    // [Step 3] 비동기 JMS 메시지 발송 (@Async 로직 실행)
	    jmsSender.sendDeployMessages(interfaceId, adapterIds);
	}

	private void createDeployHistory(String interfaceId, List<String> adapterIds) {
		try {
	        // 1. 현재 인터페이스의 최신 버전을 조회하여 +1 계산
	        int nextVersion = deployHistoryRepository.findMaxVersionByInterfaceId(interfaceId) + 1;

	        // 2. 이력 객체 생성
	        DeployHistory history = new DeployHistory();
	        history.setInterfaceId(interfaceId);
	        history.setDeployVersion(nextVersion);
	        history.setTargetAdapters(objectMapper.writeValueAsString(adapterIds));
	        history.setDeployData("{}");
	        history.setResultCode("P");
	        history.setDeployedBy("admin");
	        history.setDeployedAt(LocalDateTime.now());

	        // 3. 저장 및 반환
	        deployHistoryRepository.save(history);

	    } catch (JsonProcessingException e) {
	        log.error("배포 이력 생성 중 JSON 변환 실패: interfaceId={}, error={}", interfaceId, e.getMessage());
	        throw new RuntimeException("배포 이력 생성 중 오류가 발생했습니다.", e);
	    }
	}

	/**
	 * 배포 이력 조회
	 */
	public List<Map<String, Object>> getDeployHistory(String ifId) {
		List<DeployHistory> histories = deployHistoryRepository.findByInterfaceIdOrderByDeployedAtDesc(ifId);

		return histories.stream().map(h -> {
			Map<String, Object> map = new HashMap<>();
			map.put("deploySeq", h.getDeploySeq());
			map.put("deployedAt", h.getDeployedAt());
			map.put("deployVersion", h.getDeployVersion());
			map.put("deployedBy", h.getDeployedBy());
			map.put("resultCode", h.getResultCode());
			map.put("resultMsg", h.getResultMsg());

			// jsonb로 저장된 targetAdapters(["ADPT_01", "ADPT_02"])를 다시 List로 변환
			try {
				if (h.getTargetAdapters() != null) {
					List<String> adapterList = objectMapper.readValue(h.getTargetAdapters(), List.class);
					map.put("targetAdapters", adapterList);
				} else {
					map.put("targetAdapters", Collections.emptyList());
				}
			} catch (Exception e) {
				map.put("targetAdapters", Collections.emptyList());
			}

			return map;
		}).collect(Collectors.toList());
	}
}