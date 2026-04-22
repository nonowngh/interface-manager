package com.imc.interfacemanager.entity.interfaceinfo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "interface_prop")
@IdClass(InterfaceProp.InterfacePropId.class)
@Data
public class InterfaceProp {

	@Id
	@Column(name = "interface_id", length = 30)
	private String interfaceId;

	@Id
	@Column(name = "pattern_code", length = 10)
	private String patternCode;

	@Id
	@Column(name = "property_name", length = 100)
	private String propertyName;

	@Column(name = "property_value", length = 1000)
	private String propertyValue;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "created_by", length = 50)
	private String createdBy;

	@Column(name = "updated_by", length = 50)
	private String updatedBy;

	// JPA 표준 생명주기 콜백 (DB 독립적 시간 관리)
	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	/**
	 * 식별자(PK)를 위한 내부 정적 클래스
	 */
	public static class InterfacePropId implements Serializable {
		private static final long serialVersionUID = 1L;
		private String interfaceId;
		private String patternCode;
		private String propertyName;

		// 필수: 기본 생성자
		public InterfacePropId() {
		}

		public InterfacePropId(String interfaceId, String patternCode, String propertyName) {
			this.interfaceId = interfaceId;
			this.patternCode = patternCode;
			this.propertyName = propertyName;
		}

		// 필수: equals 구현
		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			InterfacePropId that = (InterfacePropId) o;
			return Objects.equals(interfaceId, that.interfaceId) && Objects.equals(patternCode, that.patternCode)
					&& Objects.equals(propertyName, that.propertyName);
		}

		// 필수: hashCode 구현
		@Override
		public int hashCode() {
			return Objects.hash(interfaceId, patternCode, propertyName);
		}
	}
}