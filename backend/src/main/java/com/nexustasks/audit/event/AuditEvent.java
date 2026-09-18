package com.nexustasks.audit.event;

import com.nexustasks.audit.entity.AuditAction;
import org.springframework.context.ApplicationEvent;

public class AuditEvent extends ApplicationEvent {

    private final AuditAction action;
    private final String actorPublicId;
    private final String actorEmail;
    private final String resourceType;
    private final String resourcePublicId;
    private final String ipAddress;
    private final String details;

    public AuditEvent(Object source, AuditAction action, String actorPublicId, String actorEmail,
                      String resourceType, String resourcePublicId, String ipAddress, String details) {
        super(source);
        this.action = action;
        this.actorPublicId = actorPublicId;
        this.actorEmail = actorEmail;
        this.resourceType = resourceType;
        this.resourcePublicId = resourcePublicId;
        this.ipAddress = ipAddress;
        this.details = details;
    }

    // Getters
    public AuditAction getAction() { return action; }
    public String getActorPublicId() { return actorPublicId; }
    public String getActorEmail() { return actorEmail; }
    public String getResourceType() { return resourceType; }
    public String getResourcePublicId() { return resourcePublicId; }
    public String getIpAddress() { return ipAddress; }
    public String getDetails() { return details; }
}