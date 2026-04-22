package com.imc.interfacemanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.imc.interfacemanager.dto.AdaptorInfoDto;
import com.imc.interfacemanager.entity.esb.EsbProduct;

@Repository
public interface DeployAdaptorRepository extends JpaRepository<EsbProduct, String> {

	@Query(value = "SELECT " + 
	        "    prj.pj_id AS pjId, " + 
	        "    prj.pj_name AS pjName, " + 
	        "    prd.pd_name AS pdName, " + 
	        "    prd.pd_alias AS pdAlias, " + 
	        "    prd.final_mo_status AS finalMoStatus, " + 
	        "    CASE WHEN m.interface_id IS NOT NULL THEN 'Y' ELSE 'N' END AS isMapped, " + 
	        "    (SELECT TO_CHAR(MAX(h.deployed_at), 'YYYY-MM-DD HH24:MI') " + 
	        "     FROM interface_manager.interface_deploy_hist h " + 
	        "     WHERE h.interface_id = :ifId " + 
	        "       AND h.result_code = 'S' " + 
	        "       AND h.target_adapter = prd.pd_name) AS lastDeployTime, " + // 🚀 비교 연산자로 수정
	        "    (SELECT h.deploy_version " + 
	        "     FROM interface_manager.interface_deploy_hist h " + 
	        "     WHERE h.interface_id = :ifId " + 
	        "       AND h.result_code = 'S' " + 
	        "       AND h.target_adapter = prd.pd_name " + 
	        "     ORDER BY h.deployed_at DESC LIMIT 1) AS deployVersion " +
	        "FROM esb_product prd " + 
	        "INNER JOIN esb_instance ins ON prd.pd_id = ins.in_id " + 
	        "INNER JOIN esb_project prj ON ins.pj_id = prj.pj_id " + 
	        "LEFT JOIN interface_manager.interface_adapter_map m " + 
	        "    ON prd.pd_name = m.adapter_id AND m.interface_id = :ifId " + 
	        "ORDER BY prj.pj_name DESC, prd.pd_name ASC", nativeQuery = true)
	List<AdaptorInfoDto> findAllWithMappingStatus(@Param("ifId") String ifId);
	
}
