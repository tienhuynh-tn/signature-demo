package com.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sftp")
@Data
public class SftpProperties {
    private String host;
    private int port = 22;
    private String username;
    private String password;
    private String remoteDir;
    private int connectTimeoutMs = 8000;
    private int sessionTimeoutMs = 10000;
}
