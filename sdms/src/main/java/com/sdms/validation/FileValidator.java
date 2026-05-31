package com.sdms.validation;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileValidator {

    private final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

    private final String[] ALLOWED = {
        "application/pdf",
        "image/png",
        "image/jpeg",
        "text/plain"
    };

    public void validate(MultipartFile file) {

        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (file.getSize() > MAX_SIZE) {
            throw new RuntimeException("File size > 5MB not allowed");
        }

        boolean allowed = false;

        for (String type : ALLOWED) {
            if (type.equals(file.getContentType())) {
                allowed = true;
            }
        }

        if (!allowed) {
            throw new RuntimeException(
                "Only PDF, PNG, JPG, TXT allowed"
            );
        }
    }
}
