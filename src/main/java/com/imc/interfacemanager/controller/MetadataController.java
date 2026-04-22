package com.imc.interfacemanager.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.imc.interfacemanager.service.MetadataService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/metadata")
@RequiredArgsConstructor
public class MetadataController {
	
	private final MetadataService metadataService;

    /** 전체 테이블 목록 조회 */
    @GetMapping("/tables")
    public ResponseEntity<List<String>> getTables() {
        return ResponseEntity.ok(metadataService.getTableList());
    }

    /** 특정 테이블의 컬럼 목록 조회 */
    @GetMapping("/columns/{tableName}")
    public ResponseEntity<List<String>> getColumns(@PathVariable String tableName) {
        return ResponseEntity.ok(metadataService.getColumnList(tableName));
    }

}
