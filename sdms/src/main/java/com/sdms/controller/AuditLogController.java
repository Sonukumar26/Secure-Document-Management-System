package com.sdms.controller;

import com.sdms.model.AuditLog;
import com.sdms.repository.AuditLogRepository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
  import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogRepository repo;

    public AuditLogController(AuditLogRepository repo) {
        this.repo = repo;
    }

    // @GetMapping
    // public List<AuditLog> getAll() {
    //     return repo.findAll();
    // }

    @GetMapping("/user/{username}")
    public List<AuditLog> getByUser(@PathVariable String username) {
        return repo.findByUsername(username);
    }



@GetMapping("/audit")
public Page<AuditLog> getAuditLogs(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size) {

    Pageable pageable = PageRequest.of(page, size);

    return repo.findAll(pageable);
}

@GetMapping("/audit")
public Page<AuditLog> getAuditLogs(
        @RequestParam(defaultValue = "") String search,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size) {

    Pageable pageable = PageRequest.of(page, size);

    if (search.isBlank()) {
        return repo.findAll(pageable);
    }

    return repo.findByUsernameContainingIgnoreCase(search, pageable);
}

}
