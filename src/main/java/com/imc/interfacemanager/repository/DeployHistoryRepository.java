package com.imc.interfacemanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.imc.interfacemanager.dto.DeployStatusDto;
import com.imc.interfacemanager.entity.deploy.DeployHistory;

@Repository
public interface DeployHistoryRepository extends JpaRepository<DeployHistory, Long> { // ID 타입 확인(Long/String)

	// EsbDeployHistory 엔티티에 interfaceId 필드가 있어야 작동합니다.
	List<DeployHistory> findByInterfaceIdOrderByDeployedAtDesc(String interfaceId);

	// 특정 인터페이스의 현재 최대 버전을 조회 (없으면 0 반환)
	@Query("SELECT COALESCE(MAX(h.deployVersion), 0) FROM DeployHistory h WHERE h.interfaceId = :interfaceId")
	int findMaxVersionByInterfaceId(@Param("interfaceId") String interfaceId);

	@Modifying
	@Transactional
	@Query("UPDATE DeployHistory d " + "SET d.resultCode = :resultCode, d.resultMsg = :resultMsg "
			+ "WHERE d.interfaceId = :interfaceId AND d.deployVersion = :deployVersion")
	int updateDeployResult(@Param("interfaceId") String interfaceId, @Param("deployVersion") String deployVersion,
			@Param("resultCode") String resultCode, @Param("resultMsg") String resultMsg);

//	@Query(value = "SELECT " + "    h.interface_id AS interfaceId, "
//			+ "    /* 최신 버전의 상태 중 'F'가 하나라도 있다면 전체 건수를 0으로 처리 */ " + "    CASE "
//			+ "        WHEN COUNT(CASE WHEN h.result_code = 'F' THEN 1 END) > 0 THEN 0 " + "        ELSE COUNT(*) "
//			+ "    END AS totalCount, " + "    /* 최신 버전의 상태 중 'F'가 있다면 성공 건수도 0으로 처리 */ " + "    CASE "
//			+ "        WHEN COUNT(CASE WHEN h.result_code = 'F' THEN 1 END) > 0 THEN 0 "
//			+ "        ELSE COUNT(CASE WHEN h.result_code = 'S' THEN 1 END) " + "    END AS successCount, "
//			+ "    CASE " + "        /* 1순위: 최신 버전 중에 'P'가 있다면 해당 최신 배포 시간 리턴 */ "
//			+ "        WHEN COUNT(CASE WHEN h.result_code = 'P' THEN 1 END) > 0 "
//			+ "            THEN MAX(CASE WHEN h.result_code = 'P' THEN h.deployed_at END) "
//			+ "        /* 2순위: 그 외(F 포함), 전체 이력 중 가장 최근 성공(S) 시간 리턴 */ " + "        ELSE (SELECT MAX(h3.deployed_at) "
//			+ "              FROM interface_deploy_hist h3 "
//			+ "              WHERE h3.interface_id = h.interface_id " + "                AND h3.result_code = 'S') "
//			+ "    END AS lastUpdatedAt " + "FROM interface_deploy_hist h "
//			+ "WHERE (h.interface_id, h.deploy_version) IN (" + "    SELECT h2.interface_id, MAX(h2.deploy_version) "
//			+ "    FROM interface_deploy_hist h2 " + "    WHERE h2.interface_id IN :interfaceIds "
//			+ "      AND h2.result_code IS NOT NULL " + "      AND h2.result_code != '' "
//			+ "    GROUP BY h2.interface_id" + ") " + "GROUP BY h.interface_id", nativeQuery = true)
//	List<DeployStatusDto> findDeployStatsByInterfaceIds(@Param("interfaceIds") List<String> interfaceIds);

	@Query(value = "SELECT " + "    m.interface_id AS interfaceId, " + "    m.adapter_id AS adapterId, "
			+ "    m.last_deploy_version AS lastDeployVersion, " + "    h.result_code AS lastResultCode, "
			+ "    h.deployed_at AS lastDeployedAt, " + "    s.deploy_version AS lastSuccessVersion, "
			+ "    s.deployed_at AS lastSuccessAt " + "FROM interface_adapter_map m "
			+ "LEFT JOIN interface_deploy_hist h " + "    ON m.interface_id = h.interface_id "
			+ "    AND m.adapter_id = h.target_adapter " + "    AND h.deployed_at = ( "
			+ "        SELECT MAX(sub.deployed_at) " + "        FROM interface_deploy_hist sub "
			+ "        WHERE sub.interface_id = m.interface_id " + "          AND sub.target_adapter = m.adapter_id "
			+ "    ) " + "LEFT JOIN interface_deploy_hist s " + "    ON m.interface_id = s.interface_id "
			+ "    AND m.adapter_id = s.target_adapter " + "    AND s.result_code = 'S' " + "    AND s.deployed_at = ( "
			+ "        SELECT MAX(succ.deployed_at) " + "        FROM interface_deploy_hist succ "
			+ "        WHERE succ.interface_id = m.interface_id " + "          AND succ.target_adapter = m.adapter_id "
			+ "          AND succ.result_code = 'S' " + "    ) " + "WHERE m.interface_id IN :interfaceIds "
			+ "ORDER BY m.interface_id, m.adapter_id", nativeQuery = true)
	List<DeployStatusDto> findAdapterStatusByInterfaceIds(@Param("interfaceIds") List<String> interfaceIds);

	/**
	 * 특정 인터페이스와 어댑터의 가장 최근 성공('S') 배포 이력을 조회
	 */
	@Query(value = "SELECT h.deploy_version " + "FROM interface_deploy_hist h " + "WHERE h.interface_id = :interfaceId "
			+ "  AND h.target_adapter = :targetAdapter " + "  AND h.result_code = 'S' " + "ORDER BY h.deployed_at DESC "
			+ "LIMIT 1", nativeQuery = true)
	Optional<String> findLastDeployVersion(@Param("interfaceId") String interfaceId,
			@Param("targetAdapter") String targetAdapter);

}