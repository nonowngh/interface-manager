package com.imc.interfacemanager.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "interface_adapter_map")
@IdClass(DeployAdapterMapId.class) // 1. 복합키 클래스 연결 확인
@Getter @Setter @NoArgsConstructor
public class DeployAdaptorMapping {

    @Id
    @Column(name = "interface_id") // 2. 실제 DB 컬럼명 명시
    private String interfaceId;

    @Id
    @Column(name = "adapter_id")   // 3. 실제 DB 컬럼명 명시
    private String adapterId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;
}