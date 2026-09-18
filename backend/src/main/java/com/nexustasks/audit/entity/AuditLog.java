package com.nexustasks.audit.entity;

import com.nexustasks.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AuditAction action;

    @Column(name = "actor_public_id")
    private String actorPublicId;

    @Column(name = "actor_email")
    private String actorEmail;

    @Column(name = "resource_type", length = 50)
    private String resourceType;

    @Column(name = "resource_public_id")
    private String resourcePublicId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(columnDefinition = "TEXT")
    private String details;
}