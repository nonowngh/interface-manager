package com.imc.interfacemanager.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imc.interfacemanager.dto.InterfaceInfoDto;
import com.imc.interfacemanager.dto.PatternInfoDto;
import com.imc.interfacemanager.entity.InterfaceInfoEntity;
import com.imc.interfacemanager.entity.PatternInfoEntity;
import com.imc.interfacemanager.repository.InterfaceRepository;
import com.imc.interfacemanager.repository.PatternRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterfaceService {

	private final InterfaceRepository repository;
	private final PatternRepository patternRepository;

	/**
	 * 1. 전체 목록 조회 (Read Only) DB의 Entity 리스트를 리액트가 이해할 수 있는 DTO 리스트로 변환합니다.
	 */
	@Transactional(readOnly = true)
	public List<InterfaceInfoDto> getAllInterfaces() {
		return repository.findAllByOrderByUpdatedAtDesc().stream().map(this::convertToDto) // 별도 변환 메서드 활용
				.collect(Collectors.toList());
	}

	/**
	 * 2. 상세 조회 (단건) 리액트 그리드에서 특정 행을 클릭했을 때 상세 정보를 가져오는 용도입니다.
	 */
	@Transactional(readOnly = true)
	public InterfaceInfoDto getInterfaceById(String interfaceId) {
		return repository.findById(interfaceId).map(this::convertToDto)
				.orElseThrow(() -> new RuntimeException("해당 인터페이스를 찾을 수 없습니다: " + interfaceId));
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
		InterfaceInfoEntity entity = repository.findById(interfaceId)
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
	private InterfaceInfoDto convertToDto(InterfaceInfoEntity entity) {
		PatternInfoEntity p = entity.getPattern();

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

	@Transactional
	public void saveInterface(InterfaceInfoDto dto) {
		// 1. 기존 데이터 조회 또는 신규 생성
		InterfaceInfoEntity entity = repository.findById(dto.getInterfaceId()).orElseGet(() -> {
			InterfaceInfoEntity newEntity = new InterfaceInfoEntity();
			newEntity.setInterfaceId(dto.getInterfaceId());
			newEntity.setCreatedBy("SYSTEM_ADMIN");
			return newEntity;
		});

		// 2. [변경] 레파지토리 하나로 패턴 조회
		if (dto.getPatternType() != null) {
			// 인터페이스 레파지토리 내에 새로 만든 패턴 조회 메서드 사용
			PatternInfoEntity pattern = repository.findPatternByCode(dto.getPatternType())
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
}