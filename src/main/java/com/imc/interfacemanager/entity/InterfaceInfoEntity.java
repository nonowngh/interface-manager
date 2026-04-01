package com.imc.interfacemanager.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "interface_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterfaceInfoEntity {

	@Id
	@Column(name = "interface_id", length = 30)
	private String interfaceId; // VARCHAR(30) - PK
	
	@Column(name = "interface_name", length = 30)
	private String interfaceName; // VARCHAR(30)

	@Column(name = "cron_expression", length = 30)
	private String cronExpression; // VARCHAR(30)

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pattern_type", referencedColumnName = "pattern_code")
    private PatternInfoEntity pattern;

	@Column(name = "send_system_code", length = 3, nullable = false)
	private String sendSystemCode; // CHAR(3)

	@Column(name = "recv_system_code", length = 3, nullable = false)
	private String recvSystemCode; // CHAR(3)

	@Column(name = "use_yn", length = 1, nullable = false)
	private String useYn = "N"; // CHAR(1) DEFAULT 'N'

	// --- Audit 필드 (자동 생성/수정 시간) ---

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt; // TIMESTAMP

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt; // TIMESTAMP

	@Column(name = "created_by", length = 20)
	private String createdBy;

	@Column(name = "updated_by", length = 20)
	private String updatedBy;

	/**
	 * 비즈니스 로직: 등록 전 기본값 세팅 등
	 */
	@PrePersist
	public void prePersist() {
		if (this.useYn == null) {
			this.useYn = "N";
		}
	}
}