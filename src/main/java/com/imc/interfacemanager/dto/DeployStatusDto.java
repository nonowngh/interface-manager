package com.imc.interfacemanager.dto;

import java.time.LocalDateTime;

public interface DeployStatusDto {
    String getInterfaceId();
    int getTotalCount();      // 해당 인터페이스의 전체 배포 건수
    int getSuccessCount();    // result_code = 'S'인 건수
    LocalDateTime getLastUpdatedAt(); // 가장 최근 배포(수정) 시간
}
