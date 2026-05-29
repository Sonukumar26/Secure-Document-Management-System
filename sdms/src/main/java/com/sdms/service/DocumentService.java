package com.sdms.service;

import com.sdms.model.Document;
import com.sdms.repository.DocumentRepository;

import lombok.NonNull;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final FileStorageService fileStorageService;
private final AuditService auditService;

public DocumentService(DocumentRepository documentRepository,
                       FileStorageService fileStorageService,
                       AuditService auditService) {
    this.documentRepository = documentRepository;
    this.fileStorageService = fileStorageService;
    this.auditService = auditService;
}


    public Document save(Document document) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        document.setUploadedBy(auth.getName());
        document.setUploadedAt(LocalDateTime.now());

        return documentRepository.save(document);
    }

    public List<Document> getDocumentsByUser(String username) {
        return documentRepository.findByUploadedBy(username);
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Document ID cannot be null");
        }
        documentRepository.deleteById(id);
    }

 

    public Document uploadFile(MultipartFile file, String username) throws Exception {

    String originalFileName = file.getOriginalFilename();

    // 1️⃣ Find latest version for this user + file
    Integer lastVersion = documentRepository
            .findMaxVersionByOriginalFileNameAndUploadedBy(
                    originalFileName, username);

    int newVersion = (lastVersion == null) ? 1 : lastVersion + 1;

    // 2️⃣ Deactivate previous versions
    documentRepository.deactivateOldVersions(
            originalFileName, username);

    // 3️⃣ Store file
    String storedPath = fileStorageService.storeFile(file);

    auditService.log(
        username,
        "UPLOAD",
        originalFileName,
        newVersion
    );

    // 4️⃣ Save document
    Document doc = new Document();
    doc.setFileName(file.getOriginalFilename());
    doc.setOriginalFileName(originalFileName);
    doc.setVersion(newVersion);
    doc.setActive(true);
    doc.setFilePath(storedPath);
    doc.setFileType(file.getContentType());
    doc.setFileSize(file.getSize());
    doc.setUploadedBy(username);
    doc.setUploadedAt(LocalDateTime.now());

    return documentRepository.save(doc);
}


    public Document getDocumentForDownload(@NonNull Long docid, String username, boolean isAdmin) throws Exception {
        Document doc = documentRepository.findById(docid)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        if (!isAdmin && !doc.getUploadedBy().equals(username)) {
            throw new SecurityException("You do not have permission to download this document");
        }

        return doc;
    }

        public List<Document> getVersionHistory(
            String originalFileName,
            String username,
            boolean isAdmin) {

        if (isAdmin) {
            return documentRepository
                    .findByOriginalFileNameOrderByVersionDesc(originalFileName);
        }

        return documentRepository
                .findByOriginalFileNameAndUploadedByOrderByVersionDesc(
                        originalFileName,
                        username);
    }

public Document downloadLatest(
        String fileName,
        String username,
        boolean isAdmin) {

    Document doc = documentRepository
            .findByOriginalFileNameAndActiveTrue(fileName)
            .orElseThrow(() ->
                    new RuntimeException("Active version not found"));
    auditService.log(
    username,
    "DOWNLOAD_LATEST",
    fileName,
    doc.getVersion()
);

    if (!isAdmin && !doc.getUploadedBy().equals(username)) {
        throw new SecurityException("Access denied");
    }

    return doc;
}
public Document downloadVersion(
        String fileName,
        int version,
        String username,
        boolean isAdmin) {

    Document doc = documentRepository
            .findByOriginalFileNameAndVersion(fileName, version)
            .orElseThrow(() ->
                    new RuntimeException("Version not found"));
    auditService.log(
    username,
    "DOWNLOAD_VERSION",
    fileName,
    version
);

    if (!isAdmin && !doc.getUploadedBy().equals(username)) {
        throw new SecurityException("Access denied");
    }

    return doc;
}

@SuppressWarnings("null")
@Transactional
public void rollbackVersion(
        String fileName,
        int targetVersion,
        String adminName,
        boolean isAdmin) {

    if (!isAdmin) {
        throw new SecurityException("Only admin can rollback versions");
    }

    Document target = documentRepository
            .findByOriginalFileNameAndVersion(fileName, targetVersion)
            .orElseThrow(() ->
                    new RuntimeException("Target version not found"));

    List<Document> allVersions =
            documentRepository.findByOriginalFileName(fileName);

    for (Document doc : allVersions) {
        doc.setActive(false);
    }

    target.setActive(true);

    documentRepository.saveAll(allVersions);

    auditService.log(
            adminName,
            "ROLLBACK",
            fileName,
            targetVersion
    );
}

public void validateAccess(Long docId, String username, boolean isAdmin) {

    @SuppressWarnings("null")
	Document doc = documentRepository.findById(docId)
            .orElseThrow(() -> new RuntimeException("Document not found"));

    if (!isAdmin && !doc.getUploadedBy().equals(username)) {
        throw new SecurityException("Access denied");
    }
}


}
