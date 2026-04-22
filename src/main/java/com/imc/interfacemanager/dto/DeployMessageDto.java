package com.imc.interfacemanager.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeployMessageDto {
    private String interfaceId;
    private String interfaceName;
    private String cronExpression;
    private String patternCode; // PatternInfo에서 코드만 추출
    private String sendSystemCode;
    private String recvSystemCode;
    
    private List<PropertyDto> properties;
    private List<SqlDto> sqls;

    @Data
    @AllArgsConstructor
    public static class PropertyDto {
        private String key;
        private String value;
    }

    @Data
    @AllArgsConstructor
    public static class SqlDto {
        private String sqlId;
        private String type;
        private String query;
    }
}