package com.demo.controller;

import com.demo.service.SftpSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/sync")
@RequiredArgsConstructor
public class SftpSyncController {
    private final SftpSyncService syncService;

    @PostMapping("/sftp")
    public ResponseEntity<Map<String,Object>> trigger() {
        int success = syncService.syncCreatedOnce();
        return ResponseEntity.ok(Map.of("synced", success));
    }
}
