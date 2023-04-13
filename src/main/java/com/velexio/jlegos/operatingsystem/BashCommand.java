package com.velexio.jlegos.operatingsystem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BashCommand {

    private final Log log = LogFactory.getLog(BashCommand.class);
    private String command;
    private Map<String, String> environmentVariables;

    private ProcessBuilder processBuilder;


    /**
     * Constructor for running a command without environment variables set
     *
     * @param command
     */
    public BashCommand(String command) {
        this.command = command;
        this.environmentVariables = new HashMap<>();
        processBuilder = constructProcess();
    }

    /**
     * Constructor that sets both command and environment variables required for execution
     *
     * @param command              Is the command to run
     * @param environmentVariables A map of environment variables to be set for the execution of command
     */
    public BashCommand(String command, Map<String, String> environmentVariables) {
        this.command = command;
        this.environmentVariables = environmentVariables;
        processBuilder = constructProcess();
    }

    private ProcessBuilder constructProcess() {
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
        environmentVariables.forEach((k, v) -> processBuilder.environment().put(k, v));
        return processBuilder;
    }

    /**
     * This will execute the currently set command and return a BashCommandResponse object
     *
     * @return A BashCommandResponse object, which holds all output from the command
     * @throws IOException when the process is unable to execute
     */
    public CommandResponse execute() throws IOException {
        Process process = processBuilder.start();
        BufferedReader outStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        List<String> outLines = outStream.lines().collect(Collectors.toList());
        List<String> errLines = errorStream.lines().collect(Collectors.toList());
        if (errLines.size() > 0) log.debug(String.format("Error output detected in run of command [%s]", command));
        return CommandResponse.builder()
                .stdOutLines(outLines)
                .stdErrLines(errLines)
                .success(errLines.size() == 0)
                .build();
    }

    public CommandResponse execute(String command) throws IOException {
        this.command = command;
        processBuilder = constructProcess();
        return execute();
    }

    /**
     * Allows for adding of multiple additional environment variables after object construction
     *
     * @param variableMap A Map of additional values to merge with existing environment variables
     */
    public void addEnvironmentVariables(Map<String, String> variableMap) {
        environmentVariables.putAll(variableMap);
    }

    /**
     * Allows for adding a single environment variable by name, value
     *
     * @param keyName  The name of the environment variable
     * @param keyValue String value of the variable
     */
    public void addEnvironmentVariable(String keyName, String keyValue) {
        environmentVariables.put(keyName, keyValue);
    }

}
