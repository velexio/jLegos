package com.velexio.jlegos.operatingsystem;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.velexio.jlegos.exceptions.RemoteHostInitializationException;
import com.velexio.jlegos.util.ExceptionUtils;
import com.velexio.jlegos.util.StringUtil;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;


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

    /**
     * Allows for executing a command against the remote host
     *
     * @param command The command to execute
     * @return {@code CommandResponse} object that holds the results of the command
     */
    public CommandResponse execute(String command) {

        Session session = null;
        ChannelExec channelExec = null;

        try {

            session = sshClient.getSession(username, hostname, port);

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
            return CommandResponse.builder()
                    .success(false)
                    .stdErrLines(new ArrayList<>(List.of(ExceptionUtils.getStackTrace(e))))
                    .build();
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

        /**
         * Default constructor for builder.
         *
         * @param hostname The hostname for the remote connection
         */
        public Builder(String hostname) {
            this.hostname = hostname;
            this.username = System.getProperty("user.name");
            this.port = 22;
            this.connectionTimeout = 10;
            this.sshClient = new JSch();
            this.sshConfig = new Hashtable<>();
            this.sshConfig.put("StrictHostKeyChecking", "no");
        }

        /**
         * Set the port if different than default (22)
         *
         * @param port Port number to use: Default is 22
         * @return Builder
         */
        public Builder withPort(int port) {
            this.port = port;
            return this;
        }

        /**
         * Set the username to be used for remote connection
         *
         * @param username The username
         * @return Builder
         */
        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        /**
         * Sets the password for user based authentication
         *
         * @param password The remote user's password
         * @return Builder
         */
        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        /**
         * Adding a private key via file that does not have a passphrase
         *
         * @param filePath The full path to the private key file
         * @return Builder
         * @throws RemoteHostInitializationException When key is invalid or missing
         */
        public Builder withPrivateKeyFile(String filePath) throws RemoteHostInitializationException {
            try {
                sshClient.addIdentity(filePath);
                return this;
            } catch (JSchException jse) {
                throw new RemoteHostInitializationException(jse.getLocalizedMessage());
            }
        }

        /**
         * Adding private key via a file path
         *
         * @param filePath   The full path to the private key file
         * @param passphrase The passphrase associated with the key
         * @return Builder
         * @throws RemoteHostInitializationException When key is invalid or missing
         */
        public Builder withPrivateKeyFile(String filePath, String passphrase)
                throws RemoteHostInitializationException {
            try {
                sshClient.addIdentity(filePath, passphrase);
                return this;
            } catch (JSchException jse) {
                throw new RemoteHostInitializationException(jse.getLocalizedMessage());
            }
        }

        /**
         * Sets the publice/private keys from String values, uses a default key set name
         *
         * @param privateKey String value of the private key
         * @param publicKey  String value of the public key
         * @return Builder
         * @throws RemoteHostInitializationException when keys are invalid
         */
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

        /**
         * Set the keys via String values
         *
         * @param keySetName The name of the key pair
         * @param privateKey String value of the private key
         * @param publicKey  String value of the public key
         * @return Builder
         * @throws RemoteHostInitializationException when key is invalid
         */
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

        /**
         * Allows for setting the keypair with passphrase
         *
         * @param keySetName    Name for the key pair
         * @param privateKey    String value of the private key
         * @param publicKey     String value of the public key
         * @param keyPassphrase The string value of passphrase used when key was generated
         * @return Returns Builder object
         * @throws RemoteHostInitializationException when unable to initialize because of invalid keys
         */
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

        /**
         * Set the strict host checking option to "yes"
         *
         * @return Builder object
         */
        public Builder withStrictHostCheckingOn() {
            this.sshConfig.put("StrictHostKeyChecking", "yes");
            return this;
        }

        /**
         * Sets the connection timeout in number of seconds
         *
         * @param timeoutSeconds The number of seconds to wait for timeout
         * @return Builder
         */
        public Builder withConnectTimeout(int timeoutSeconds) {
            this.connectionTimeout = timeoutSeconds;
            return this;
        }

        /**
         * Builds the RemoteHost object
         *
         * @return RemoteHost
         */
        public RemoteHost build() {
            return new RemoteHost(this);
        }

    }

}
