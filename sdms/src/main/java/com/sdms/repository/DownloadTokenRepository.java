package com.sdms.repository;

import com.sdms.model.DownloadToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DownloadTokenRepository
        extends JpaRepository<DownloadToken, Long> {

    Optional<DownloadToken> findByToken(String token);
}
