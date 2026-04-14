package com.imc.interfacemanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.imc.interfacemanager.entity.InterfaceProp;

@Repository
public interface InterfacePropRepository extends JpaRepository<InterfaceProp, InterfaceProp.InterfacePropId> {
    
    // 특정 인터페이스 아이디에 속한 모든 설정 정보 조회
    List<InterfaceProp> findByInterfaceId(String interfaceId);

    // 특정 인터페이스 + 패턴 조합의 설정 정보 조회 (필요 시)
    List<InterfaceProp> findByInterfaceIdAndPatternCode(String interfaceId, String patternCode);
    
    @Modifying(clearAutomatically = true, flushAutomatically = true) // 👈 flush 추가
    @Query("DELETE FROM InterfaceProp p WHERE p.interfaceId = :interfaceId")
    void deleteByInterfaceId(@Param("interfaceId") String interfaceId);
    
    /**
     * 특정 패턴 코드에서 사용 중인 속성 키(PropertyName) 목록을 중복 없이 조회
     * InterfaceProp(p)와 InterfaceMaster(m)가 interfaceId로 연결되어 있다고 가정합니다.
     */
    @Query("SELECT DISTINCT p.propertyName " +
           "FROM InterfaceProp p " +
           "WHERE p.patternCode = :patternCode " +
           "AND p.propertyName IS NOT NULL " +
           "ORDER BY p.propertyName ASC")
    List<String> findDistinctPropertyNamesByPatternCode(@Param("patternCode") String patternCode);
}