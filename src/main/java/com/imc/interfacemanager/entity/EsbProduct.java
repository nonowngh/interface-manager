package com.imc.interfacemanager.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;

@Entity
@Table(name = "esb_product")
@Getter
public class EsbProduct {
	@Id
    @Column(name = "pd_id")
    private String pdId;

    @Column(name = "pd_name")
    private String pdName;

    @Column(name = "pd_alias")
    private String pdAlias;

    @Column(name = "final_mo_status")
    private String finalMoStatus;
}