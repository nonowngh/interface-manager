package com.imc.interfacemanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.imc.interfacemanager.entity.InterfaceInfoEntity;
import com.imc.interfacemanager.entity.PatternInfoEntity;

@Repository
public interface InterfaceRepository extends JpaRepository<InterfaceInfoEntity, String> {

	// 패턴 코드로 패턴 엔티티 하나를 가져오는 쿼리 추가
    @Query("SELECT p FROM PatternInfoEntity p WHERE p.patternCode = :code")
    Optional<PatternInfoEntity> findPatternByCode(@Param("code") String code);

    // [성능 최적화] 목록 조회 시 N+1 방지를 위한 Fetch Join
    @Query("SELECT i FROM InterfaceInfoEntity i JOIN FETCH i.pattern")
    List<InterfaceInfoEntity> findAllWithPattern();

	// 인터페이스 ID 또는 명칭으로 검색 (JPQL)
	// 참고: entity에 interfaceName이 있다고 가정하거나 다른 필드로 조건 검색 시 유용
	@Query("SELECT i FROM InterfaceInfoEntity i WHERE i.interfaceId LIKE %:keyword%")
	List<InterfaceInfoEntity> searchByKeyword(@Param("keyword") String keyword);

	// 특정 인터페이스의 사용 여부만 업데이트 (벌크 연산)
	// 영속성 컨텍스트 무시하고 직접 DB 수정 시 사용 (성능 최적화)
	@Modifying
	@Query("UPDATE InterfaceInfoEntity i SET i.useYn = :useYn, i.updatedBy = :updater WHERE i.interfaceId = :id")
	int updateUseStatus(@Param("id") String id, @Param("useYn") String useYn, @Param("updater") String updater);
	
	// 수정일(modifiedDate) 기준 내림차순(Desc) 정렬
	List<InterfaceInfoEntity> findAllByOrderByUpdatedAtDesc();
}