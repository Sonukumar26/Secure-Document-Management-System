package com.sdms.service;

import com.sdms.model.AuditLog;
import com.sdms.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String username,
                    String action,
                    String fileName,
                    Integer version) {

        AuditLog log = new AuditLog(
                username, action, fileName, version);

        auditLogRepository.save(log);
    }
}
