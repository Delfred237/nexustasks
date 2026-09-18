package com.nexustasks.audit.listener;

import com.nexustasks.audit.entity.AuditLog;
import com.nexustasks.audit.event.AuditEvent;
import com.nexustasks.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditEventListener {

    private final AuditLogRepository auditLogRepository;

    @EventListener
    public void onAuditEvent(AuditEvent event) {
        AuditLog auditLog = AuditLog.builder()
                .action(event.getAction())
                .actorPublicId(event.getActorPublicId())
                .actorEmail(event.getActorEmail())
                .resourceType(event.getResourceType())
                .resourcePublicId(event.getResourcePublicId())
                .ipAddress(event.getIpAddress())
                .details(event.getDetails())
                .build();

        auditLogRepository.save(auditLog);
        log.debug("Audit logged: {} by {}", event.getAction(), event.getActorEmail());
    }
}