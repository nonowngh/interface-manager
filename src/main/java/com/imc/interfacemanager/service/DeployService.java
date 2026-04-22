package com.imc.interfacemanager.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imc.interfacemanager.dto.AdaptorInfoDto;
import com.imc.interfacemanager.dto.DeployMessageDto;
import com.imc.interfacemanager.dto.DeployResultDto;
import com.imc.interfacemanager.entity.deploy.DeployAdaptorMapping;
import com.imc.interfacemanager.entity.deploy.DeployHistory;
import com.imc.interfacemanager.entity.interfaceinfo.InterfaceInfo;
import com.imc.interfacemanager.entity.interfaceinfo.InterfaceProp;
import com.imc.interfacemanager.entity.interfaceinfo.InterfaceSql;
import com.imc.interfacemanager.messaging.JmsSender;
import com.imc.interfacemanager.repository.DeployAdaptorMappingRepository;
import com.imc.interfacemanager.repository.DeployAdaptorRepository;
import com.imc.interfacemanager.repository.DeployHistoryRepository;
import com.imc.interfacemanager.repository.InterfacePropRepository;
import com.imc.interfacemanager.repository.InterfaceRepository;
import com.imc.interfacemanager.repository.InterfaceSqlRepository;

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
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final InterfaceRepository interfaceRepository;
	private final InterfacePropRepository propRepository;
	private final InterfaceSqlRepository sqlRepository;

	/**
	 * 전체 어댑터 상태 및 매핑 정보 조회
	 */
	public List<AdaptorInfoDto> getFullAdaptorStatus(String interfaceId) {
		return deployAdaptorRepository.findAllWithMappingStatus(interfaceId);
	}

	@Transactional
	public void processAsyncDeploy(String interfaceId, List<String> adapterIds, String deployVersion) {
		// 매핑 정보 동기화 (기존 삭제 후 일괄 저장)
		deployAdaptorMappingRepository.deleteByInterfaceId(interfaceId);

		// 리스트를 엔티티 리스트로 변환
		List<DeployAdaptorMapping> mappings = adapterIds.stream().map(adapterId -> {
			DeployAdaptorMapping mapping = new DeployAdaptorMapping();
			mapping.setInterfaceId(interfaceId);
			mapping.setAdapterId(adapterId);
			mapping.setCreatedBy("admin");
			mapping.setLastDeployVersion(deployVersion);
			return mapping;
		}).collect(Collectors.toList());

		deployAdaptorMappingRepository.saveAll(mappings);

		log.info("인터페이스 [{}]에 어댑터 {}개 매핑 완료", interfaceId, adapterIds.size());

		try {
			DeployMessageDto deployPayload = buildDeployPayload(interfaceId);
			String deployJson = objectMapper.writeValueAsString(deployPayload);

			List<DeployHistory> histories = adapterIds.stream().map(adapterId -> {
				DeployHistory history = new DeployHistory();
				history.setInterfaceId(interfaceId);
				history.setDeployVersion(deployVersion);
				history.setTargetAdapter(adapterId);
				history.setDeployData(deployJson);
				history.setResultCode("P");
				history.setDeployedBy("admin");
				history.setDeployedAt(LocalDateTime.now());
				return history;
			}).collect(Collectors.toList());
			deployHistoryRepository.saveAll(histories);

			jmsSender.sendDeployMessages(deployPayload, adapterIds);
		} catch (Exception e) {
			updateDeployResult(DeployResultDto.builder().interfaceId(interfaceId).resultCode("F")
					.resultMessage(e.getMessage()).deployVersion(deployVersion).build());
		}
	}

	private DeployMessageDto buildDeployPayload(String interfaceId) {
		InterfaceInfo info = interfaceRepository.findById(interfaceId)
				.orElseThrow(() -> new EntityNotFoundException("Interface not found: " + interfaceId));

		List<InterfaceProp> props = propRepository.findByInterfaceId(interfaceId);
		List<InterfaceSql> sqls = sqlRepository.findByInterfaceId(interfaceId);

		return DeployMessageDto.builder().interfaceId(info.getInterfaceId()).interfaceName(info.getInterfaceName())
				.cronExpression(info.getCronExpression()).patternCode(info.getPattern().getPatternCode())
				.sendSystemCode(info.getSendSystemCode()).recvSystemCode(info.getRecvSystemCode())
				.properties(props.stream()
						.map(p -> new DeployMessageDto.PropertyDto(p.getPropertyName(), p.getPropertyValue()))
						.collect(Collectors.toList()))
				.sqls(sqls.stream().map(s -> new DeployMessageDto.SqlDto(s.getSqlId(), s.getSqlType(), s.getSqlQuery()))
						.collect(Collectors.toList()))
				.build();
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
			map.put("targetAdapter", h.getTargetAdapter());

//			// jsonb로 저장된 targetAdapters(["ADPT_01", "ADPT_02"])를 다시 List로 변환
//			try {
//				if (h.getTargetAdapters() != null) {
//					List<String> adapterList = objectMapper.readValue(h.getTargetAdapters(), List.class);
//					map.put("targetAdapters", adapterList);
//				} else {
//					map.put("targetAdapters", Collections.emptyList());
//				}
//			} catch (Exception e) {
//				map.put("targetAdapters", Collections.emptyList());
//			}

			return map;
		}).collect(Collectors.toList());
	}

	public void updateDeployResult(DeployResultDto resultDto) {
		deployHistoryRepository.updateDeployResult(resultDto.getInterfaceId(), resultDto.getDeployVersion(),
				resultDto.getResultCode(), resultDto.getResultMessage());
	}
}