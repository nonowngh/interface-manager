package com.imc.interfacemanager.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imc.interfacemanager.dto.DeployStatusDto;
import com.imc.interfacemanager.dto.InterfaceInfoDto;
import com.imc.interfacemanager.dto.InterfacePropDto;
import com.imc.interfacemanager.dto.InterfaceSqlDto;
import com.imc.interfacemanager.dto.PatternInfoDto;
import com.imc.interfacemanager.entity.InterfaceInfo;
import com.imc.interfacemanager.entity.InterfaceProp;
import com.imc.interfacemanager.entity.InterfaceSql;
import com.imc.interfacemanager.entity.PatternInfo;
import com.imc.interfacemanager.repository.DeployHistoryRepository;
import com.imc.interfacemanager.repository.InterfacePropRepository;
import com.imc.interfacemanager.repository.InterfaceRepository;
import com.imc.interfacemanager.repository.InterfaceSqlRepository;
import com.imc.interfacemanager.repository.PatternRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterfaceService {

	private final InterfaceRepository repository;
	private final PatternRepository patternRepository;
	private final DeployHistoryRepository deployHistoryRepository;
	private final InterfacePropRepository propRepository;
	private final InterfaceSqlRepository sqlRepository;

	/**
	 * 1. 전체 목록 조회
	 */
	@Transactional(readOnly = true)
	public List<InterfaceInfoDto> getAllInterfaces() {
		// 1. 모든 인터페이스 엔티티 조회
		List<InterfaceInfo> entities = repository.findAllByOrderByUpdatedAtDesc();

		// 2. 모든 ID를 리스트로 추출
		List<String> ids = entities.stream().map(InterfaceInfo::getInterfaceId).collect(Collectors.toList());

		// 3. 통계 정보를 한 번의 쿼리로 대량 조회 (N+1 방지)
		Map<String, DeployStatusDto> statsMap = deployHistoryRepository.findDeployStatsByInterfaceIds(ids).stream()
				.collect(Collectors.toMap(DeployStatusDto::getInterfaceId, s -> s));
		// 4. 변환 메서드에 Map을 전달하여 조립
		return entities.stream().map(entity -> convertToDto(entity, statsMap.get(entity.getInterfaceId())))
				.collect(Collectors.toList());
	}

	/**
	 * 2. 상세 조회 (단건)
	 */
	@Transactional(readOnly = true)
	public InterfaceInfoDto getInterfaceById(String interfaceId) {
		// 1. 인터페이스 기본 정보 조회
		InterfaceInfo entity = repository.findById(interfaceId)
				.orElseThrow(() -> new RuntimeException("해당 인터페이스를 찾을 수 없습니다: " + interfaceId));
		// 2. 설정 정보(Properties) 조회
		List<InterfacePropDto> props = propRepository.findByInterfaceId(interfaceId).stream()
				.map(p -> InterfacePropDto.builder().interfaceId(p.getInterfaceId()).patternCode(p.getPatternCode())
						.propertyName(p.getPropertyName()).propertyValue(p.getPropertyValue()).build())
				.collect(Collectors.toList());
		// 3. SQL 정보 조회 (추가된 부분) -------------------------------------------
		List<InterfaceSqlDto> sqls = sqlRepository.findByInterfaceId(interfaceId).stream()
				.map(s -> InterfaceSqlDto.builder().interfaceId(s.getInterfaceId()).sqlId(s.getSqlId())
						.sqlType(s.getSqlType()).sqlQuery(s.getSqlQuery()).build())
				.collect(Collectors.toList());
		// 4. 배포 통계 정보 조회
		List<DeployStatusDto> stats = deployHistoryRepository.findDeployStatsByInterfaceIds(Arrays.asList(interfaceId));
		DeployStatusDto stat = stats.isEmpty() ? null : stats.get(0);
		// 5. DTO 조립
		InterfaceInfoDto dto = convertToDto(entity, stat);
		dto.setProperties(props);
		dto.setSqls(sqls);
		return dto;
	}

	/**
	 * 3. 조건 검색 (키워드 검색) 인터페이스 ID나 시스템 코드로 필터링할 때 사용합니다.
	 */
	@Transactional(readOnly = true)
	public List<InterfaceInfoDto> searchInterfaces(String keyword) {
		return repository.searchByKeyword(keyword).stream().map(this::convertToDto).collect(Collectors.toList());
	}

	/**
	 * 4. 사용여부 토글 수정 (Partial Update) JPA의 Dirty Checking을 활용하여 특정 필드만 수정합니다.
	 */
	@Transactional
	public void updateUseYn(String interfaceId, String useYn) {
		// 1. 수정할 엔티티 조회
		InterfaceInfo entity = repository.findById(interfaceId)
				.orElseThrow(() -> new RuntimeException("수정할 인터페이스가 없습니다. ID: " + interfaceId));

		// 2. 엔티티의 상태 변경 (Setter 호출)
		// @Transactional이 붙어있으므로 메서드 종료 시 영속성 컨텍스트가
		// 변경을 감지하고 DB에 UPDATE 문을 자동으로 실행합니다.
		entity.setUseYn(useYn);
		entity.setUpdatedBy("SYSTEM_ADMIN"); // 수정자 정보 업데이트
	}

	/**
	 * [Helper] Entity -> DTO 변환 (자바 8 스타일) 필드가 많으므로 별도 메서드로 분리하는 것이 유지보수에 좋습니다.
	 */
	private InterfaceInfoDto convertToDto(InterfaceInfo entity) {
		PatternInfo p = entity.getPattern();

		return InterfaceInfoDto.builder().interfaceId(entity.getInterfaceId()).interfaceName(entity.getInterfaceName())
				.patternType(p != null ? p.getPatternCode() : null).patternName(p != null ? p.getPatternName() : null)
				.interfaceType(p != null ? p.getInterfaceType().name() : null)
				.interfaceTypeName(p != null ? p.getInterfaceType().getDescription() : "") // ex: "배치"
				.cronExpression(entity.getCronExpression()).patternType(entity.getPattern().getPatternCode())
				.patternName(entity.getPattern().getPatternName()).sendSystemCode(entity.getSendSystemCode())
				.updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)
				.recvSystemCode(entity.getRecvSystemCode()).useYn(entity.getUseYn()).createdBy(entity.getCreatedBy())
				.updatedBy(entity.getUpdatedBy()).deployStatus(entity.getDeployStatus())
				.lastDeployAt(entity.getLastDeployAt() != null ? entity.getLastDeployAt().toString() : null)
				.lastDeployBy(entity.getLastDeployBy()).build();
	}

	private InterfaceInfoDto convertToDto(InterfaceInfo entity, DeployStatusDto stat) {
		PatternInfo p = entity.getPattern();
		String deployStatus = "N";
		if (stat != null && stat.getLastUpdatedAt() != null) {
			if (stat.getTotalCount() > 0) {
				if (stat.getSuccessCount() == stat.getTotalCount())
					deployStatus = "Y";
				else
					deployStatus = "P";
			}
		}

		return InterfaceInfoDto.builder().interfaceId(entity.getInterfaceId()).interfaceName(entity.getInterfaceName())
				.patternType(p != null ? p.getPatternCode() : null).patternName(p != null ? p.getPatternName() : null)
				.interfaceType(p != null ? p.getInterfaceType().name() : null)
				.interfaceTypeName(p != null ? p.getInterfaceType().getDescription() : "") // ex: "배치"
				.cronExpression(entity.getCronExpression()).patternType(entity.getPattern().getPatternCode())
				.patternName(entity.getPattern().getPatternName()).sendSystemCode(entity.getSendSystemCode())
				.updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)
				.recvSystemCode(entity.getRecvSystemCode()).useYn(entity.getUseYn()).createdBy(entity.getCreatedBy())
				.updatedBy(entity.getUpdatedBy()).deployStatus(entity.getDeployStatus()).deployStatus(deployStatus)
				.deployTotalCount(stat != null ? stat.getTotalCount() : 0)
				.deploySuccessCount(stat != null ? stat.getSuccessCount() : 0)
				.lastDeployAt(
						stat != null && stat.getLastUpdatedAt() != null ? stat.getLastUpdatedAt().toString() : null)
				.build();
	}

	@Transactional
	public void saveInterface(InterfaceInfoDto dto) {
		// 1. 기존 데이터 조회 또는 신규 생성
		InterfaceInfo entity = repository.findById(dto.getInterfaceId()).orElseGet(() -> {
			InterfaceInfo newEntity = new InterfaceInfo();
			newEntity.setInterfaceId(dto.getInterfaceId());
			newEntity.setCreatedBy("SYSTEM_ADMIN");
			return newEntity;
		});

		// 2. [변경] 레파지토리 하나로 패턴 조회
		if (dto.getPatternType() != null) {
			// 인터페이스 레파지토리 내에 새로 만든 패턴 조회 메서드 사용
			PatternInfo pattern = repository.findPatternByCode(dto.getPatternType())
					.orElseThrow(() -> new RuntimeException("존재하지 않는 패턴 코드: " + dto.getPatternType()));

			entity.setPattern(pattern);
		}

		// 3. 필드 매핑 및 저장
		entity.setInterfaceName(dto.getInterfaceName());
		entity.setCronExpression(dto.getCronExpression());
		entity.setSendSystemCode(dto.getSendSystemCode());
		entity.setRecvSystemCode(dto.getRecvSystemCode());
		entity.setUseYn(dto.getUseYn());
		entity.setUpdatedBy("SYSTEM_ADMIN");

		repository.save(entity);
	}

	// 패턴 정보 조회
	@Transactional(readOnly = true)
	public List<PatternInfoDto> getAllPatterns() {
		return patternRepository.findAllByOrderBySortOrderAsc().stream().map(PatternInfoDto::fromEntity)
				.collect(Collectors.toList());
	}

	/**
	 * 특정 인터페이스의 설정 정보만 별도로 조회할 때
	 */
	@Transactional(readOnly = true)
	public List<InterfacePropDto> getInterfaceProps(String interfaceId) {
		return propRepository.findByInterfaceId(interfaceId).stream()
				.map(prop -> InterfacePropDto.builder().interfaceId(prop.getInterfaceId())
						.patternCode(prop.getPatternCode()).propertyName(prop.getPropertyName())
						.propertyValue(prop.getPropertyValue()).build())
				.collect(Collectors.toList());
	}

	@Transactional
	public void saveInterfaceWithProps(InterfaceInfoDto dto) {
		// 1. 인터페이스 마스터 엔티티 조회 (수정 시간 갱신을 위해 영속성 컨텍스트에 올림)
	    InterfaceInfo master = repository.findById(dto.getInterfaceId())
	            .orElseThrow(() -> new RuntimeException("인터페이스를 찾을 수 없습니다: " + dto.getInterfaceId()));

	    // 2. 기본 정보 업데이트 로직 수행 (기존 saveInterface 로직 호출 혹은 직접 구현)
	    updateMasterInfo(master, dto);
	    
	    // 2. 상세 설정(Properties) 처리: 삭제 후 저장
	    propRepository.deleteByInterfaceId(dto.getInterfaceId());
	    if (dto.getProperties() != null && !dto.getProperties().isEmpty()) {
	        List<InterfaceProp> props = dto.getProperties().stream().map(pDto -> {
	            InterfaceProp prop = new InterfaceProp();
	            prop.setInterfaceId(dto.getInterfaceId());
	            prop.setPatternCode(dto.getPatternType());
	            prop.setPropertyName(pDto.getPropertyName());
	            prop.setPropertyValue(pDto.getPropertyValue());
	            prop.setCreatedBy("SYSTEM_ADMIN");
	            prop.setUpdatedBy("SYSTEM_ADMIN");
	            return prop;
	        }).collect(Collectors.toList());
	        propRepository.saveAllAndFlush(props); 
	    }
	    // 3. SQL 정보 처리 (추가된 부분): 삭제 후 저장 -------------------------------
	    sqlRepository.deleteByInterfaceId(dto.getInterfaceId());
	    if (dto.getSqls() != null && !dto.getSqls().isEmpty()) {
	        List<InterfaceSql> sqlEntities = dto.getSqls().stream().map(sDto -> {
	            return InterfaceSql.builder()
	                    .interfaceId(dto.getInterfaceId())
	                    .sqlId(sDto.getSqlId())
	                    .sqlType(sDto.getSqlType())
	                    .sqlQuery(sDto.getSqlQuery())
	                    .build();
	        }).collect(Collectors.toList());
	        sqlRepository.saveAll(sqlEntities);
	    }
	    
	    master.setUpdatedAt(LocalDateTime.now());
	    master.setUpdatedBy("SYSTEM_ADMIN");
	    repository.saveAndFlush(master);
	}
	
	private void updateMasterInfo(InterfaceInfo master, InterfaceInfoDto dto) {
	    master.setInterfaceName(dto.getInterfaceName());
	    master.setCronExpression(dto.getCronExpression());
	    master.setSendSystemCode(dto.getSendSystemCode());
	    master.setRecvSystemCode(dto.getRecvSystemCode());
	    master.setUseYn(dto.getUseYn());
	}

	@Transactional(readOnly = true)
	public List<String> getDistinctKeysByPattern(String patternCode) {
		return propRepository.findDistinctPropertyNamesByPatternCode(patternCode);
	}
}