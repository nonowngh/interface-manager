package com.imc.interfacemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterfacePropDto {
    private String interfaceId;
    private String patternCode;
    private String propertyName;
    private String propertyValue;
}