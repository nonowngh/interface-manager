package com.imc.interfacemanager.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class UnDeployRequest {
	private String interfaceId;
    private List<AdapterStatusDto> adapters;
    @Data
    @AllArgsConstructor
    public static class AdapterStatusDto {
        private String adapterId;
        private boolean operational;
    }
}