package com.imc.interfacemanager.dto;

import java.time.LocalDateTime;

public interface DeployHistoryDto {
    Long getDeploySeq();
    LocalDateTime getDeployedAt();
    String getDeployVersion();
    String getDeployedBy();
    String getResult();
    
    // DB에 JSON 문자열로 저장되어 있다면 Service에서 List로 변환이 필요할 수 있습니다.
    String getTargetAdaptersRaw(); 
}