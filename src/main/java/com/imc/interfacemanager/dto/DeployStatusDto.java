package com.imc.interfacemanager.dto;

import java.time.LocalDateTime;

public interface DeployStatusDto {
	String getInterfaceId();
    String getAdapterId();
    String getLastDeployVersion();   // 현재 매핑된 버전
    String getLastResultCode();      // 현재 상태 (P, S, F 등)
    LocalDateTime getLastDeployedAt();
    String getLastSuccessVersion();  // 마지막으로 'S'였던 버전
    LocalDateTime getLastSuccessAt(); // 마지막으로 'S'였던 시간
}
