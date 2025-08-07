package com.demo.service;

import com.demo.entity.Application;
import com.demo.repository.ApplicationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SftpSyncServiceImpl implements SftpSyncService {
    private final ApplicationRepository appRepo;
    private final SftpClient sftpClient;
    private final Environment env;

    @Transactional
    @Override
    public int syncCreatedOnce() {
        int batchSize = Integer.parseInt(env.getProperty("sync.sftp.batch-size", "100"));
        List<Application> batch = appRepo.findTop100ByStatusOrderByCreatedDateAsc(Application.Status.CREATED);

        int success = 0;
        for (Application app : batch) {
            try {
                // Option A: Upload raw Base64 (simple; downstream can decode)
                byte[] bytes = app.getSignatureImage().getBytes(StandardCharsets.UTF_8);
                String remoteName = app.getId().toString() + ".b64";

                // Option B: Decode and upload image bytes (uncomment if needed)
                // byte[] bytes = Base64.getDecoder().decode(app.getSignatureImage());
                // String remoteName = app.getId().toString() + ".png";

                sftpClient.uploadBytes(bytes, remoteName);

                app.setStatus(Application.Status.SUCCESS);
                success++;
            } catch (Exception ex) {
                log.warn("SFTP upload failed for app {}: {}", app.getId(), ex.getMessage());
                app.setStatus(Application.Status.FAILED);
            }
        }
        // persist all status changes
        appRepo.saveAll(batch);
        return success;
    }
}
