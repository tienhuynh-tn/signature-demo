package com.demo.service;

import com.demo.config.SftpProps;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class SftpClient {

    private final SftpProps props;

    /** Open and return a connected ChannelSftp. Caller must disconnect it (and its Session). */
    private ChannelSftp connect() throws JSchException, InterruptedException {
        int attempts = 0, max = 5;
        long backoffMs = 1000;
        JSch jsch = new JSch();

        while (true) {
            try {
                Session session = jsch.getSession(props.username(), props.host(), props.port());
                session.setPassword(props.password());
                Properties cfg = new Properties();
                cfg.put("StrictHostKeyChecking", props.strictHostKeyChecking() ? "yes" : "no");
                session.setConfig(cfg);

                session.connect(props.connectTimeoutMs());               // throws if refused
                ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
                sftp.connect(props.connectTimeoutMs());
                return sftp;
            } catch (JSchException e) {
                if (++attempts >= max) throw e;
                Thread.sleep(backoffMs * attempts); // linear backoff
            }
        }
    }

    /** Ensure remote directory exists (best-effort). */
    private void ensureDir(ChannelSftp sftp, String remoteDir) throws SftpException {
        try {
            sftp.cd(remoteDir);
        } catch (SftpException e) {
            // mkdir -p behavior
            String[] parts = remoteDir.split("/");
            String path = "";
            for (String p : parts) {
                if (p == null || p.isBlank()) continue;
                path += "/" + p;
                try { sftp.cd(path); }
                catch (SftpException ignored) { sftp.mkdir(path); sftp.cd(path); }
            }
        }
    }

    public void uploadBytes(byte[] data, String remoteName) {
        ChannelSftp sftp = null;
        Session session = null;
        try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
            sftp = connect();
            session = sftp.getSession(); // keep a handle to close
            ensureDir(sftp, props.remoteDir());
            sftp.put(in, remoteName, ChannelSftp.OVERWRITE);
        } catch (SftpException | JSchException | IOException e) {
            throw new IllegalStateException("SFTP upload failed: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (sftp != null && sftp.isConnected()) sftp.disconnect();
            if (session != null && session.isConnected()) session.disconnect();
        }
    }
}
