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
		List<InterfaceInfoDto> list = interfaceService.getAllInterfaces();
		return ResponseEntity.ok(list); // 200 OK
	}

	/**
	 * 2. 상세 조회 (READ - Single) GET /api/v1/interfaces/{id}
	 */
	@GetMapping("/{id}")
	public ResponseEntity<InterfaceInfoDto> getInterface(@PathVariable String id) {
		return ResponseEntity.ok(interfaceService.getInterfaceById(id));
	}

	/**
	 * 3. 저장 및 수정 (CREATE / UPDATE) POST /api/v1/interfaces 리액트에서 신규/수정 구분 없이 이
	 * 엔드포인트로 보낼 때의 처리입니다.
	 */
	@PostMapping
	public ResponseEntity<String> saveInterface(@RequestBody InterfaceInfoDto dto) {
		interfaceService.saveInterface(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body("저장 성공"); // 201 Created
	}

	/**
	 * 4. 사용여부 토글 수정 (PARTIAL UPDATE) PATCH /api/v1/interfaces/{id} 리액트의
	 * handleToggleUse 함수와 매핑됩니다.
	 */
	@PatchMapping("/{id}")
	public ResponseEntity<Void> toggleUseYn(@PathVariable String id, @RequestBody Map<String, String> body) {

		String useYn = body.get("useYn");
		interfaceService.updateUseYn(id, useYn);
		return ResponseEntity.noContent().build(); // 204 No Content
	}

	@GetMapping("/patterns")
	public ResponseEntity<List<PatternInfoDto>> getAllPatterns() {
		List<PatternInfoDto> list = interfaceService.getAllPatterns();
		return ResponseEntity.ok(list); // 200 OK
	}
	
}