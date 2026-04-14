package com.imc.interfacemanager.dto;

import com.imc.interfacemanager.entity.PatternInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatternInfoDto {

    private String patternCode;     // 'P01', 'P02' 등
    private String patternName;     // 'DB to DB' 등
    private String interfaceType;   // 'REALTIME' 또는 'BATCH' (Enum name)
    private String interfaceTypeName; // '실시간' 또는 '배치' (Enum description)
    private String patternDesc;     // 패턴 상세 설명
    private Integer sortOrder;      // 정렬 순서
    private String useYn;           // 'Y' / 'N'

    /**
     * [Static Factory Method] 
     * Entity를 DTO로 변환하는 편의 메서드입니다. 
     * 서비스 단에서 .map(PatternInfoDto::fromEntity) 형태로 깔끔하게 사용 가능합니다.
     */
    public static PatternInfoDto fromEntity(PatternInfo entity) {
        if (entity == null) return null;

        return PatternInfoDto.builder()
                .patternCode(entity.getPatternCode())
                .patternName(entity.getPatternName())
                .interfaceType(entity.getInterfaceType().name())
                .interfaceTypeName(entity.getInterfaceType().getDescription())
                .patternDesc(entity.getPatternDesc())
                .sortOrder(entity.getSortOrder())
                .useYn(entity.getUseYn())
                .build();
    }
}