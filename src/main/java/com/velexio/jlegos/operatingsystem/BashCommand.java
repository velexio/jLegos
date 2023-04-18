package com.velexio.jlegos.operatingsystem;

import com.velexio.jlegos.exceptions.CommandExecutionException;
import com.velexio.jlegos.util.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class that allows for executing Unix commands within a bash shell
 */
public class BashCommand {

    private final Log log = LogFactory.getLog(BashCommand.class);
    private String command;
    private Map<String, String> environmentVariables;

    private ProcessBuilder processBuilder;


    /**
     * Constructor for running a command without environment variables set
     *
     * @param command The command to run
     */
    public BashCommand(String command) {
        this.command = command;
        this.environmentVariables = new HashMap<>();
        processBuilder = constructProcess();
    }

    /**
     * Constructor for when command is not known at construction. Use the overloaded execute(String c) method
     * when constructing command without command
     */
    public BashCommand() {
        this.environmentVariables = new HashMap<>();
    }

    /**
     * Constructor for when environment variables are set at creation. Use this when anticipated to run
     * several commands with same instance.
     *
     * @param environmentVariables Map containing all environment variables to be set inside shell
     */
    public BashCommand(Map<String, String> environmentVariables) {
        this.environmentVariables = environmentVariables;
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
     * @throws CommandExecutionException when the process is unable to execute
     */
    public CommandResponse execute() {
        try {
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
        } catch (IOException ioe) {
            return CommandResponse.builder()
                    .success(false)
                    .stdErrLines(new ArrayList<>(List.of(ExceptionUtils.getStackTrace(ioe))))
                    .build();
        }
    }

    /**
     * Executes the specified command and returns {@code CommandResponse} object
     *
     * @param command The command that is desired to be executed
     * @return CommandResponse object that holds stdout stderr lines
     * @throws CommandExecutionException when execution is unable to start a process
     */
    public CommandResponse execute(String command) throws CommandExecutionException {
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
