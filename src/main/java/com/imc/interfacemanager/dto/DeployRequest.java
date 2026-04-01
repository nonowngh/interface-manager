package com.imc.interfacemanager.dto;

import java.util.List;

import lombok.Data;

@Data
public class DeployRequest {
    private String interfaceId;
    private List<String> adapterIds;
}