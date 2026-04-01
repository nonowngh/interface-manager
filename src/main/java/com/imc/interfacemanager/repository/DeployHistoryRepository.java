package com.imc.interfacemanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.imc.interfacemanager.entity.DeployHistory;

@Repository
public interface DeployHistoryRepository extends JpaRepository<DeployHistory, Long> { // ID 타입 확인(Long/String)

	// EsbDeployHistory 엔티티에 interfaceId 필드가 있어야 작동합니다.
	List<DeployHistory> findByInterfaceIdOrderByDeployedAtDesc(String interfaceId);
	
	// 특정 인터페이스의 현재 최대 버전을 조회 (없으면 0 반환)
    @Query("SELECT COALESCE(MAX(h.deployVersion), 0) FROM DeployHistory h WHERE h.interfaceId = :interfaceId")
    int findMaxVersionByInterfaceId(@Param("interfaceId") String interfaceId);

}