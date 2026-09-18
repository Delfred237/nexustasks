package com.nexustasks.audit.dto;

import com.nexustasks.audit.entity.AuditAction;
import com.nexustasks.audit.entity.AuditLog;

import java.time.Instant;

public record AuditLogResponse(
        String publicId,
        AuditAction action,
        String actorEmail,
        String resourceType,
        String resourcePublicId,
        String ipAddress,
        String details,
        Instant createdAt
) {
    public static AuditLogResponse from(AuditLog log) {
        return new AuditLogResponse(
                log.getPublicId(),
                log.getAction(),
                log.getActorEmail(),
                log.getResourceType(),
                log.getResourcePublicId(),
                log.getIpAddress(),
                log.getDetails(),
                log.getCreatedAt()
        );
    }
}