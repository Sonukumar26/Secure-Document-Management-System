package com.sdms.service;

import com.sdms.model.DownloadToken;
import com.sdms.model.Document;
import com.sdms.repository.DownloadTokenRepository;
import com.sdms.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DownloadTokenService {

    private final DownloadTokenRepository tokenRepo;
    private final DocumentRepository documentRepo;

    public DownloadTokenService(
            DownloadTokenRepository tokenRepo,
            DocumentRepository documentRepo) {
        this.tokenRepo = tokenRepo;
        this.documentRepo = documentRepo;
    }

    public String generateToken(Long documentId, int minutes) {

        @SuppressWarnings("null")
		Document doc = documentRepo.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        DownloadToken token = new DownloadToken();
        token.setToken(UUID.randomUUID().toString());
        token.setDocumentId(doc.getId());
        token.setExpiresAt(LocalDateTime.now().plusMinutes(minutes));

        tokenRepo.save(token);
        return token.getToken();
    }

    @SuppressWarnings("null")
	public Document validateAndGetDocument(String tokenValue) {

        DownloadToken token = tokenRepo.findByToken(tokenValue)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (token.isUsed()) {
            throw new RuntimeException("Token already used");
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        token.setUsed(true);
        tokenRepo.save(token);

        return documentRepo.findById(token.getDocumentId())
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }
}
