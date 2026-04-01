package com.imc.interfacemanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.imc.interfacemanager.entity.PatternInfoEntity;

public interface PatternRepository extends JpaRepository<PatternInfoEntity, String> {
    // 정렬 순서대로 가져오기 위해 추가
    List<PatternInfoEntity> findAllByOrderBySortOrderAsc();
}