package com.sdms.controller;

import com.sdms.model.Document;
import com.sdms.service.DocumentService;
import com.sdms.service.DownloadTokenService;
import com.sdms.validation.FileValidator;

import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final DownloadTokenService downloadTokenService;
@Autowired
private FileValidator fileValidator;

    public DocumentController(DownloadTokenService downloadTokenService, DocumentService documentService) {
        this.downloadTokenService = downloadTokenService;
        this.documentService = documentService;
    }


@PostMapping("/uploads")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public Document upload(
        @RequestParam MultipartFile file,
        Authentication auth) throws Exception {

    // STEP 14 VALIDATION
    fileValidator.validate(file);

    return documentService.uploadFile(file, auth.getName());
}


    

    // 🔐 ADMIN + USER — View documents
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<Document> getAll(Principal principal) {

        String username = principal.getName();

        boolean isAdmin = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return isAdmin
                ? documentService.getAllDocuments()
                : documentService.getDocumentsByUser(username);
    }

    // 🔐 ADMIN — Delete document
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        documentService.delete(id);
    }

        @SuppressWarnings("null")
        @GetMapping("/download/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Resource> download(
            @PathVariable Long id,
            Principal principal) throws Exception {

        String username = principal.getName();
Authentication auth = SecurityContextHolder.getContext().getAuthentication();

boolean isAdmin = auth.getAuthorities()
        .stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
       

        Document doc = documentService
                .getDocumentForDownload(id, username, isAdmin);

        Path path = Paths.get(doc.getFilePath());
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("File not found");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + doc.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(doc.getFileType()))
                .body(resource);
    }

    // 🔐 ADMIN + USER — View document version history
@GetMapping("/versions/{originalFileName}")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public List<Document> getVersionHistory(
        @PathVariable String originalFileName,
        Principal principal) {

    String username = principal.getName();

    boolean isAdmin = SecurityContextHolder.getContext()
            .getAuthentication()
            .getAuthorities()
            .stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

    return documentService
            .getVersionHistory(originalFileName, username, isAdmin);
}

@GetMapping("/download/latest/{fileName}")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public ResponseEntity<Resource> downloadLatest(
        @PathVariable String fileName,
        Principal principal) throws Exception {

    String username = principal.getName();
    boolean isAdmin = isAdmin();

    Document doc = documentService
            .downloadLatest(fileName, username, isAdmin);

    return buildDownloadResponse(doc);
}

@SuppressWarnings("null")
private ResponseEntity<Resource> buildDownloadResponse(Document doc)
        throws Exception {

    Path path = Paths.get(doc.getFilePath());
    Resource resource = new UrlResource(path.toUri());

    if (!resource.exists()) {
        throw new RuntimeException("File not found on disk");
    }

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" +
                            doc.getOriginalFileName() + "\"")
            .contentType(MediaType.parseMediaType(doc.getFileType()))
            .body(resource);
}
private boolean isAdmin() {
    return SecurityContextHolder.getContext()
            .getAuthentication()
            .getAuthorities()
            .stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
}

@PostMapping("/rollback/{fileName}/v/{version}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<String> rollback(
        @PathVariable String fileName,
        @PathVariable int version,
        Principal principal) {

    String adminName = principal.getName();

    boolean isAdmin = SecurityContextHolder.getContext()
            .getAuthentication()
            .getAuthorities()
            .stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

    documentService.rollbackVersion(
            fileName,
            version,
            adminName,
            isAdmin
    );

    return ResponseEntity.ok(
            "Rolled back " + fileName + " to version " + version);
}

@PostMapping("/generate-link/{docId}")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public ResponseEntity<String> generateLink(
        @PathVariable Long docId,
        @RequestParam(defaultValue = "10") int minutes,
        Principal principal) {

    String username = principal.getName();

    boolean isAdmin = SecurityContextHolder.getContext()
            .getAuthentication()
            .getAuthorities()
            .stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

    // 🔒 Optional: ownership check (recommended)
    documentService.validateAccess(docId, username, isAdmin);

    String token = downloadTokenService.generateToken(docId, minutes);

    String link =
        "http://localhost:8080/api/public/download?token=" + token;

    return ResponseEntity.ok(link);
}



}