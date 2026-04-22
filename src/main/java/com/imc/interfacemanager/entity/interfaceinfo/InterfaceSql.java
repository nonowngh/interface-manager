package com.imc.interfacemanager.entity.interfaceinfo;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "interface_sql")
@IdClass(InterfaceSql.InterfaceSqlId.class) // 내부 클래스를 ID 클래스로 지정
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterfaceSql {

	@Id
	@Column(name = "interface_id", length = 30)
	private String interfaceId;

	@Id
	@Column(name = "sql_id", length = 50)
	private String sqlId;

	@Column(name = "sql_type", length = 6, nullable = false)
	private String sqlType;

	@Column(name = "sql_query", nullable = false, columnDefinition = "TEXT")
	private String sqlQuery;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	// --- 복합키 클래스 (내부 클래스로 정의) ---
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class InterfaceSqlId implements Serializable {
		private static final long serialVersionUID = 1L;
		private String interfaceId;
		private String sqlId;
	}
}