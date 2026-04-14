package com.imc.interfacemanager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imc.interfacemanager.dto.InterfaceInfoDto;
import com.imc.interfacemanager.dto.PatternInfoDto;
import com.imc.interfacemanager.service.InterfaceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/interfaces")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:5173") // React 개발 서버 허용
public class InterfaceController {

	private final InterfaceService interfaceService;

	/**
	 * 1. 목록 조회 (READ) GET /api/v1/interfaces
	 */
	@GetMapping
	public ResponseEntity<List<InterfaceInfoDto>> getAllInterfaces() {
		return ResponseEntity.ok(interfaceService.getAllInterfaces());
	}

	/**
	 * 2. 상세 조회 (READ - Single) GET /api/v1/interfaces/{id}
	 */
	@GetMapping("/{interfaceId}")
	public ResponseEntity<InterfaceInfoDto> getInterfaceById(@PathVariable String interfaceId) {
		return ResponseEntity.ok(interfaceService.getInterfaceById(interfaceId));
	}

	/**
	 * 3. 저장 및 수정 (CREATE / UPDATE) POST /api/v1/interfaces 리액트에서 신규/수정 구분 없이 이
	 * 엔드포인트로 보낼 때의 처리입니다.
	 */
	@PostMapping
	public ResponseEntity<String> saveInterface(@RequestBody InterfaceInfoDto dto) {
		interfaceService.saveInterfaceWithProps(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body("저장 성공");
	}

	/**
	 * 4. 사용여부 토글 수정 (PARTIAL UPDATE) PATCH /api/v1/interfaces/{id} 리액트의
	 * handleToggleUse 함수와 매핑됩니다.
	 */
	@PatchMapping("/{id}")
	public ResponseEntity<Void> toggleUseYn(@PathVariable String id, @RequestBody Map<String, String> body) {
		String useYn = body.get("useYn");
		interfaceService.updateUseYn(id, useYn);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/patterns")
	public ResponseEntity<List<PatternInfoDto>> getAllPatterns() {
		return ResponseEntity.ok(interfaceService.getAllPatterns());
	}

	/**
	 * 5. 패턴별 사용 중인 속성 키(Key) 목록 조회 (중복 제거) GET
	 * /api/v1/interfaces/properties/keys/{patternCode}
	 */
	@GetMapping("/properties/keys/{patternCode}")
	public ResponseEntity<List<String>> getExistingKeysByPattern(@PathVariable String patternCode) {
		// Service를 통해 중복 없는 키 목록을 가져옵니다.
		List<String> keys = interfaceService.getDistinctKeysByPattern(patternCode);
		return ResponseEntity.ok(keys);
	}

	/**
	 * 5. 키워드 검색
	 */
	@GetMapping("/search")
	public ResponseEntity<List<InterfaceInfoDto>> searchInterfaces(@RequestParam String keyword) {
		return ResponseEntity.ok(interfaceService.searchInterfaces(keyword));
	}

}