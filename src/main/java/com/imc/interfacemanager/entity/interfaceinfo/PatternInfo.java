package com.imc.interfacemanager.entity.interfaceinfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.imc.interfacemanager.constant.InterfaceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pattern_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatternInfo {

	@Id
	@Column(name = "pattern_code", length = 10)
	private String patternCode; // 'P01', 'P02' 등

	@Column(name = "pattern_name", nullable = false, length = 50)
	private String patternName; // 'DB to DB' 등

	@Enumerated(EnumType.STRING) // DB에 문자열(REALTIME/BATCH)로 저장
	@Column(name = "interface_type", nullable = false)
	private InterfaceType interfaceType;

	@Column(name = "pattern_desc", length = 200)
	private String patternDesc;

	@Column(name = "use_yn", length = 1)
	private String useYn = "Y";

	@Column(name = "sort_order")
	private Integer sortOrder = 0;
}