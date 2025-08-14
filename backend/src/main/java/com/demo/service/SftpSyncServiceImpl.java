package com.demo.service;

import com.demo.entity.Application;
import com.demo.repository.ApplicationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SftpSyncServiceImpl implements SftpSyncService {
    private final ApplicationRepository appRepo;
    private final SftpClient sftpClient;

    @Transactional
    @Override
    public int syncCreatedOnce() {
        List<Application> batch = appRepo.findTop100ByStatusOrderByCreatedDateAsc(Application.Status.CREATED);

        int success = 0;
        for (Application app : batch) {
            try {
                // If stored as data URI, strip the header
                String b64 = app.getSignatureImage();
                if (b64 != null && b64.startsWith("data:image")) {
                    b64 = b64.substring(b64.indexOf(',') + 1);
                }

                byte[] bytes = Base64.getDecoder().decode(b64);
                String remoteName = app.getId() + ".png"; // or .jpg based on content

                sftpClient.uploadBytes(bytes, remoteName);
                app.setStatus(Application.Status.SUCCESS);
                success++;
            } catch (Exception ex) {
                log.warn("SFTP upload failed for app {}: {}", app.getId(), ex.getMessage());
                app.setStatus(Application.Status.SUCCESS);
            }
        }
        appRepo.saveAll(batch);
        if (success > 0) {
            log.info("SFTP sync completed for {} applications", success);
        } else {
            log.info("No applications to sync");
        }
        return success;
    }
}