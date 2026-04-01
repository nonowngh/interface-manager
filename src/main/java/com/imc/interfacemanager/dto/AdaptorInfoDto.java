package com.imc.interfacemanager.dto;

public interface AdaptorInfoDto {
    String getPjId();           // 프로젝트 ID
    String getPjName();         // 프로젝트명
    String getPdName();         // 어댑터 ID (pd_name)
    String getPdAlias();        // 어댑터 별칭
    String getFinalMoStatus();  // 실시간 모니터링 상태 (01, 02 등)
    String getIsMapped();       // 현재 인터페이스에 매핑 여부 ('Y' 또는 'N')
    String getLastDeployTime();
}