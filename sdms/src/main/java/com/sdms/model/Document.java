package com.sdms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Original name (same across versions)
    @Column(nullable = false)
    private String originalFileName;

    // Stored filename (UUID based)
    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    private long fileSize;

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String uploadedBy;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    // 🔥 Version number
    @Column(nullable = false)
    private int version;

    // 🔥 Active (latest version)
    @Column(nullable = false)
    private boolean active = true;

    // Constructors
    public Document() {}

    public Document(
            Long id,
            String originalFileName,
            String fileName,
            String fileType,
            long fileSize,
            String filePath,
            String uploadedBy,
            LocalDateTime uploadedAt,
            int version,
            boolean active
    ) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = uploadedAt;
        this.version = version;
        this.active = active;
    }

    // Getters & Setters (add remaining ones as needed)

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    
}
