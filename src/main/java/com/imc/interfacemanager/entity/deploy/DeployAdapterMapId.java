package com.imc.interfacemanager.entity.deploy;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DeployAdapterMapId implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String interfaceId;
    private String adapterId;
}