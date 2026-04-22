package com.imc.interfacemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeployResultDto {
    private String interfaceId;
    private String adapterId;
    private String resultCode;  // "S" (Success), "F" (Failure)
    private String resultMessage;
    private String deployVersion;
}
