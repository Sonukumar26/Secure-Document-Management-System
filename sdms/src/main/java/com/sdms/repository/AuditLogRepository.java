package com.sdms.repository;

import com.sdms.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUsername(String username);


public interface AuditRepository
        extends JpaRepository<AuditLog, Long> {
}

Page<AuditLog> findByUsernameContainingIgnoreCase(
        String username,
        Pageable pageable);

    
}
