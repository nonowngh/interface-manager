package com.imc.interfacemanager.dto;

import com.imc.interfacemanager.constant.InterfaceType; // Enum 임포트

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterfaceInfoDto {
    // 1. 기본 식별 정보
    private String interfaceId;      
    private String interfaceName;    
    
    // [변경] 리액트에서는 "REALTIME" 문자열로 주고받지만, 
    // 내부적으로 Enum의 description(실시간/배치)을 꺼내 쓰기 편하게 구성합니다.
    private String interfaceType;    // "REALTIME" 또는 "BATCH"
    private String interfaceTypeName; // "실시간" 또는 "배치" (화면 표시용 하이브리드 필드)

    // 2. 패턴 정보
    private String patternType;      // pattern_code (ex: P01)
    private String patternName;      // pattern_name (ex: DB to DB)

    // 3. 실행 및 시스템 정보
    private String cronExpression;   
    private String sendSystemCode;   
    private String recvSystemCode;   
    private String useYn;            

    // 4. Audit 필드
    private String createdAt;
    private String updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * [Helper] Enum 변환 편의 메서드
     * 서비스 레이어에서 interfaceType(String)을 Enum으로 안전하게 바꿀 때 사용합니다.
     */
    public InterfaceType getInterfaceTypeEnum() {
        return interfaceType != null ? InterfaceType.valueOf(interfaceType) : null;
    }
    
    // 기존 Boolean 헬퍼 메서드 유지
    public boolean isUseYnBoolean() {
        return "Y".equalsIgnoreCase(this.useYn);
    }

    public void setUseYnFromBoolean(boolean useYn) {
        this.useYn = useYn ? "Y" : "N";
    }
}