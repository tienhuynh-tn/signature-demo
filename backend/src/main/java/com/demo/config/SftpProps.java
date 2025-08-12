package com.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sftp")
public record SftpProps(
        String host,
        int port,
        String username,
        String password,
        String remoteDir,
        int connectTimeoutMs,
        boolean strictHostKeyChecking
) {}
