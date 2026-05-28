package com.sdms.repository;

import com.sdms.model.Document;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface DocumentRepository extends JpaRepository<Document, Long> {

 List<Document> findByUploadedBy(String uploadedBy);

    // 🔥 Get all active documents for user
    List<Document> findByUploadedByAndActiveTrue(String uploadedBy);

    // 🔥 ADMIN view — all latest documents
    List<Document> findByActiveTrue();


    List<Document> findByOriginalFileNameAndUploadedByOrderByVersionDesc(
            String originalFileName,
            String uploadedBy);

            @Query("""
    SELECT MAX(d.version)
    FROM Document d
    WHERE d.originalFileName = :fileName
      AND d.uploadedBy = :username
""")
Integer findMaxVersionByOriginalFileNameAndUploadedBy(
        String fileName, String username);

@Modifying
@Query("""
    UPDATE Document d
    SET d.active = false
    WHERE d.originalFileName = :fileName
      AND d.uploadedBy = :username
""")
void deactivateOldVersions(String fileName, String username);

List<Document> findByOriginalFileNameOrderByVersionDesc(String fileName);

Optional<Document> findByOriginalFileNameAndActiveTrue(String fileName);

Optional<Document> findByOriginalFileNameAndVersion(
        String fileName,
        int version);

        List<Document> findByOriginalFileName(String originalFileName);
}
