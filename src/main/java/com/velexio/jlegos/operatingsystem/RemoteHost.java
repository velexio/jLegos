package com.velexio.jlegos.operatingsystem;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.velexio.jlegos.exceptions.RemoteHostExecutionException;
import com.velexio.jlegos.exceptions.RemoteHostInitializationException;
import com.velexio.jlegos.util.StringUtil;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Hashtable;


/**
 * Class that allows for easily interacting with a remote host that will accept SSH connections
 */
public class RemoteHost {

    private final JSch sshClient;
    private final String hostname;
    private final String username;
    private final String password;
    private final int port;
    private final Hashtable<String, String> sshConfig;
    private final int connectionTimeout;

    private RemoteHost(Builder builder) {
        sshClient = builder.sshClient;
        this.hostname = builder.hostname;
        this.username = builder.username;
        this.password = builder.password;
        this.port = builder.port;
        this.sshConfig = builder.sshConfig;
        this.connectionTimeout = builder.connectionTimeout;
    }

    public CommandResponse execute(String command) throws RemoteHostExecutionException {

        Session session = null;
        ChannelExec channelExec = null;

        try {

            session = sshClient.getSession(hostname, username, port);

            if (StringUtil.hasValue(password)) {
                session.setPassword(password);
            }

            session.setConfig(sshConfig);
            session.connect(connectionTimeout);
            channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(command);
            ByteArrayOutputStream stdOutStream = new ByteArrayOutputStream();
            ByteArrayOutputStream stdErrStream = new ByteArrayOutputStream();
            channelExec.setOutputStream(stdOutStream);
            channelExec.setErrStream(stdErrStream);
            channelExec.connect(connectionTimeout);

            while (channelExec.isConnected()) {
                Thread.sleep(100);
            }

            return new CommandResponse.CommandResponseBuilder()
                    .stdOutLines(Arrays.asList(stdOutStream.toString().split("\n")))
                    .stdErrLines(Arrays.asList(stdErrStream.toString().split("\n")))
                    .success(stdErrStream.toByteArray().length < 1)
                    .build();


        } catch (JSchException | InterruptedException e) {
            throw new RemoteHostExecutionException(e.getLocalizedMessage());
        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (channelExec != null) {
                channelExec.disconnect();
            }
        }

    }

    public static class Builder {
        private final String hostname;
        private String username;
        private String password;
        private int port;
        private final JSch sshClient;
        private final Hashtable<String, String> sshConfig;
        private int connectionTimeout;

        public Builder(String hostname) {
            this.hostname = hostname;
            this.username = System.getProperty("user.name");
            this.port = 22;
            this.connectionTimeout = 10;
            this.sshClient = new JSch();
            this.sshConfig = new Hashtable<>();
            this.sshConfig.put("StrictHostKeyChecking", "no");
        }

        public Builder withPort(int port) {
            this.port = port;
            return this;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withPrivateKeyFile(String filePath) throws RemoteHostInitializationException {
            try {
                sshClient.addIdentity(filePath);
                return this;
            } catch (JSchException jse) {
                throw new RemoteHostInitializationException(jse.getLocalizedMessage());
            }
        }

        public Builder withPrivateKeyFile(String filePath, String passphrase)
                throws RemoteHostInitializationException {
            try {
                sshClient.addIdentity(filePath, passphrase);
                return this;
            } catch (JSchException jse) {
                throw new RemoteHostInitializationException(jse.getLocalizedMessage());
            }
        }

        public Builder withKeysFromStrings(String privateKey, String publicKey)
                throws RemoteHostInitializationException {
            try {
                sshClient.addIdentity("ssh-key",
                        privateKey.getBytes(StandardCharsets.UTF_8),
                        publicKey.getBytes(StandardCharsets.UTF_8),
                        null);
                return this;
            } catch (JSchException jse) {
                throw new RemoteHostInitializationException(jse.getLocalizedMessage());
            }
        }

        public Builder withKeysFromStrings(String keySetName, String privateKey, String publicKey)
                throws RemoteHostInitializationException {
            try {
                sshClient.addIdentity(keySetName,
                        privateKey.getBytes(StandardCharsets.UTF_8),
                        publicKey.getBytes(StandardCharsets.UTF_8),
                        null);
                return this;
            } catch (JSchException jse) {
                throw new RemoteHostInitializationException(jse.getLocalizedMessage());
            }
        }

        public Builder withKeysFromStrings(String keySetName, String privateKey,
                                           String publicKey, String keyPassphrase)
                throws RemoteHostInitializationException {
            try {
                sshClient.addIdentity(keySetName,
                        privateKey.getBytes(StandardCharsets.UTF_8),
                        publicKey.getBytes(StandardCharsets.UTF_8),
                        keyPassphrase.getBytes(StandardCharsets.UTF_8));
                return this;
            } catch (JSchException jse) {
                throw new RemoteHostInitializationException(jse.getLocalizedMessage());
            }
        }

        public Builder withStrictHostCheckingOn() {
            this.sshConfig.put("StrictHostKeyChecking", "yes");
            return this;
        }

        public Builder withConnectTimeout(int timeoutSeconds) {
            this.connectionTimeout = timeoutSeconds;
            return this;
        }

        public RemoteHost build() {
            return new RemoteHost(this);
        }

    }

}
