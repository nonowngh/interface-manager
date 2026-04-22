package com.imc.interfacemanager.entity.esb;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "esb_instance")
@Getter @Setter
public class EsbInstance {
	@Id
    @Column(name = "in_id")
    private String inId;

    @Column(name = "pj_id") // 조인용 컬럼도 그냥 일반 필드로 선언
    private String pjId;
}