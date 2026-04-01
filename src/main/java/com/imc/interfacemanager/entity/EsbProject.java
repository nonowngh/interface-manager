package com.imc.interfacemanager.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "esb_project") // 스키마 명시 권장
@Getter @Setter
public class EsbProject {
	@Id
    @Column(name = "pj_id")
    private String pjId;

    @Column(name = "pj_name")
    private String pjName;
}