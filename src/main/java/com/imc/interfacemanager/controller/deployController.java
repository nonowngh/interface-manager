package com.imc.interfacemanager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.imc.interfacemanager.dto.AdaptorInfoDto;
import com.imc.interfacemanager.dto.DeployRequest;
import com.imc.interfacemanager.dto.UnDeployRequest;
import com.imc.interfacemanager.service.DeployService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/deploy")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*") // 개발 환경에 따라 조정
public class deployController {

	private final DeployService deployService;

	/**
	 * 전체 어댑터(제품)의 상태 및 프로젝트 정보 목록 조회 GET /api/deploy/adaptors/status
	 */
	@GetMapping("/adaptors/status/{interfaceId}")
	public ResponseEntity<List<AdaptorInfoDto>> getStatus(@PathVariable String interfaceId) {
		List<AdaptorInfoDto> result = deployService.getFullAdaptorStatus(interfaceId);
		return ResponseEntity.ok(result);
	}

	@PostMapping("/execute")
	public ResponseEntity<?> executeDeploy(@RequestBody DeployRequest request) {
	    deployService.processAsyncDeploy(request.getInterfaceId(), request.getAdapterIds(), request.getDeployVersion());
	    return ResponseEntity.ok().build();
	}
	
	@PostMapping("/cancel")
	public ResponseEntity<?> executeCancel(@RequestBody UnDeployRequest request) {
	    deployService.processAsyncUnDeploy(request.getInterfaceId(), request.getAdapters());
	    return ResponseEntity.ok().build();
	}

	/**
	 * 특정 인터페이스의 최근 배포 이력 조회 GET /api/adapters/history/IF_001
	 */
	@GetMapping("/history/{interfaceId}")
	public ResponseEntity<List<Map<String, Object>>> getDeployHistory(@PathVariable String interfaceId) {
		List<Map<String, Object>> history = deployService.getDeployHistory(interfaceId);
		return ResponseEntity.ok(history);
	}
}
