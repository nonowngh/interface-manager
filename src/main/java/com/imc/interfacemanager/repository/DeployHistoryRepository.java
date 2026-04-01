package com.imc.interfacemanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.imc.interfacemanager.entity.DeployHistory;

@Repository
public interface DeployHistoryRepository extends JpaRepository<DeployHistory, Long> { // ID 타입 확인(Long/String)

	// EsbDeployHistory 엔티티에 interfaceId 필드가 있어야 작동합니다.
	List<DeployHistory> findByInterfaceIdOrderByDeployedAtDesc(String interfaceId);

}