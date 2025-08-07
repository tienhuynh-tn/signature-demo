package com.demo.service;

import com.demo.config.SftpProperties;
import com.jcraft.jsch.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class SftpClient {
    private final SftpProperties props;

    public void uploadBytes(byte[] content, String remoteFilePath) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(props.getUsername(), props.getHost(), props.getPort());
        session.setPassword(props.getPassword());
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no"); // demo-friendly
        session.setConfig(config);
        session.connect(props.getSessionTimeoutMs());

        Channel channel = session.openChannel("sftp");
        channel.connect(props.getConnectTimeoutMs());
        ChannelSftp sftp = (ChannelSftp) channel;

        try (InputStream is = new ByteArrayInputStream(content)) {
            ensureDirExists(sftp, props.getRemoteDir());
            sftp.put(is, props.getRemoteDir() + "/" + remoteFilePath);
        } finally {
            sftp.disconnect();
            session.disconnect();
        }
    }

    private void ensureDirExists(ChannelSftp sftp, String path) throws SftpException {
        String[] folders = path.split("/");
        String current = "";
        for (String f : folders) {
            if (f.isBlank()) continue;
            current += "/" + f;
            try { sftp.cd(current); }
            catch (SftpException e) { sftp.mkdir(current); sftp.cd(current); }
        }
    }
}
