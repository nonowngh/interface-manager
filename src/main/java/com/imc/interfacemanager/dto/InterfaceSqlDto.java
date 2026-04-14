package com.imc.interfacemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterfaceSqlDto {
    private String interfaceId;
    private String sqlId;
    private String sqlType;
    private String sqlQuery; // 프론트의 sqlContent가 이리로 매핑됨
}