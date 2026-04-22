package com.imc.interfacemanager.entity.deploy;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType; // 중요 import

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "interface_deploy_hist", schema = "interface_manager")
/** [핵심 설정] jsonb 타입을 사용하기 위해 타입을 정의합니다. **/
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Getter
@Setter
@NoArgsConstructor
public class DeployHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deploy_seq")
    private Integer deploySeq;

    @Column(name = "interface_id", nullable = false, length = 30)
    private String interfaceId;

    @Column(name = "deploy_version", nullable = false)
    private String deployVersion;

    /** [핵심 설정] 정의한 "jsonb" 타입을 이 필드에 적용합니다. **/
    @Type(type = "jsonb")
    @Column(name = "deploy_data", columnDefinition = "jsonb", nullable = false)
    private String deployData; 

    @Column(name = "target_adapter", nullable = false)
    private String targetAdapter;

    @Column(name = "result_code", length = 1)
    private String resultCode;

    @Column(name = "result_msg", columnDefinition = "text")
    private String resultMsg;

    @CreationTimestamp
    @Column(name = "deployed_at", updatable = false)
    private LocalDateTime deployedAt;

    @Column(name = "deployed_by", length = 20)
    private String deployedBy;
}