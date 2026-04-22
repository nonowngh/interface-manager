package com.imc.interfacemanager.entity.interfaceinfo;

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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "interface_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterfaceInfo {

    @Id
    @Column(name = "interface_id", length = 30)
    private String interfaceId; 
    
    @Column(name = "interface_name", length = 100)
    private String interfaceName; 

    @Column(name = "cron_expression", length = 30)
    private String cronExpression; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pattern_type", referencedColumnName = "pattern_code", nullable = false)
    private PatternInfo pattern;

    @Column(name = "send_system_code", length = 3, nullable = false)
    private String sendSystemCode; 

    @Column(name = "recv_system_code", length = 3, nullable = false)
    private String recvSystemCode; 

    @Column(name = "use_yn", length = 1, nullable = false)
    private String useYn = "N"; 

    // --- 🚀 배포(Deploy) 관련 추가 컬럼 ---

    @Column(name = "deploy_status", length = 1, nullable = false)
    @Builder.Default
    private String deployStatus = "N"; // 'Y': 배포완료, 'N': 미배포 또는 배포필요

    @Column(name = "last_deploy_at")
    private LocalDateTime lastDeployAt; // 최종 성공 배포 일시

    @Column(name = "last_deploy_by", length = 20)
    private String lastDeployBy; // 최종 배포자 ID

    // --- Audit 필드 ---

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; 

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; 

    @Column(name = "created_by", length = 20)
    private String createdBy;

    @Column(name = "updated_by", length = 20)
    private String updatedBy;

    @PrePersist
    public void prePersist() {
        if (this.useYn == null) this.useYn = "N";
        if (this.deployStatus == null) this.deployStatus = "N";
    }
}