package com.imc.interfacemanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.imc.interfacemanager.entity.InterfaceSql;
import com.imc.interfacemanager.entity.InterfaceSql.InterfaceSqlId;

@Repository
public interface InterfaceSqlRepository extends JpaRepository<InterfaceSql, InterfaceSqlId> {
    // 인터페이스 ID로 등록된 모든 SQL 목록 조회
    List<InterfaceSql> findByInterfaceId(String interfaceId);
    
    @Modifying(clearAutomatically = true) // 벌크 연산 후 영속성 컨텍스트 초기화
    @Query("DELETE FROM InterfaceSql s WHERE s.interfaceId = :interfaceId")
    void deleteByInterfaceId(@Param("interfaceId") String interfaceId);
}