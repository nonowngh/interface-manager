package com.imc.interfacemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.imc.interfacemanager.entity.deploy.DeployAdapterMapId;
import com.imc.interfacemanager.entity.deploy.DeployAdaptorMapping;

@Repository
public interface DeployAdaptorMappingRepository extends JpaRepository<DeployAdaptorMapping, DeployAdapterMapId> {

	@Modifying
    @Transactional
    void deleteByInterfaceId(String interfaceId);
	

}
