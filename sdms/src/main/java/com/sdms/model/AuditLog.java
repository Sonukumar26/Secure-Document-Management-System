package com.sdms.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;   // admin / user1
    private String action;     // UPLOAD, DOWNLOAD, DELETE, ROLLBACK
    private String fileName;   // document name
    private Integer version;   // nullable
    private LocalDateTime timestamp;

    public AuditLog() {}

    public AuditLog(String username, String action,
                    String fileName, Integer version) {
        this.username = username;
        this.action = action;
        this.fileName = fileName;
        this.version = version;
        this.timestamp = LocalDateTime.now();
    }

    // getters & setters
}
