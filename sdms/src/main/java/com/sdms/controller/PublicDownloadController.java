package com.sdms.controller;

import com.sdms.model.Document;
import com.sdms.service.DownloadTokenService;
import java.nio.file.Paths;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/public")
public class PublicDownloadController {

    private final DownloadTokenService tokenService;

    public PublicDownloadController(DownloadTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @SuppressWarnings("null")
	@GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam String token)
            throws Exception {

        Document doc = tokenService.validateAndGetDocument(token);

        Path path = Paths.get(doc.getFilePath());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + doc.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(doc.getFileType()))
                .body(resource);
    }

    
}
