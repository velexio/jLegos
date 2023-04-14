package com.velexio.jlegos.operatingsystem;

import com.velexio.jlegos.exceptions.CommandExecutionException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BashCommandTest {

    @Test
    void execute() throws CommandExecutionException {
        String testCommand = "date";
        BashCommand bashCommand = new BashCommand(testCommand);
        CommandResponse response = bashCommand.execute();
        assertTrue(response.isSuccess());
    }

    @Test
    void execute_withEnvVarsWorks() throws CommandExecutionException {
        Map<String, String> envVars = new HashMap<>(Map.of("TEST_RUN", "YES"));
        String command = "echo $TEST_RUN";
        BashCommand bashCommand = new BashCommand(command, envVars);
        CommandResponse response = bashCommand.execute();
        assertTrue(response.isSuccess(), "Command should have returned success");
        assertEquals("YES", response.getStdOutLines().get(0), "Command output was not what expected");
    }

    @Test
    void execute_SecondCommandWorks() throws CommandExecutionException {
        Map<String, String> envVars = new HashMap<>(Map.of("TEST_RUN", "YES", "TEST_VAR_2", "testing123"));
        String command1 = "echo $TEST_RUN";
        String command2 = "echo $TEST_VAR_2";
        BashCommand bashCommand = new BashCommand(command1, envVars);
        bashCommand.execute();
        CommandResponse response = bashCommand.execute(command2);
        assertTrue(response.isSuccess(), "Command should have returned success");
        assertEquals("testing123", response.getStdOutLines().get(0), "Command output was not what expected");
    }

    @Test
    void execute_MergeEnvVarsWorks() throws CommandExecutionException {
        Map<String, String> envVars = new HashMap<>(Map.of("TEST_RUN", "YES", "TEST_VAR_2", "testing123"));
        String command1 = "echo $TEST_RUN";
        String command2 = "echo $TEST_VAR_2";
        BashCommand bashCommand = new BashCommand(command1, envVars);
        bashCommand.execute();
        bashCommand.addEnvironmentVariables(new HashMap<>(Map.of("TEST_VAR_2", "testVar2", "TEST_VAR_3", "testVar3")));
        CommandResponse response = bashCommand.execute(command2);
        assertTrue(response.isSuccess(), "Command should have returned success");
        assertEquals("testVar2", response.getStdOutLines().get(0), "Command output was not what expected");
    }


    @Test
    void execute_AddEnvVarWorks() throws CommandExecutionException {
        Map<String, String> envVars = new HashMap<>(Map.of("TEST_RUN", "YES"));
        String command = "echo $TEST_RUN";
        String command2 = "echo $ADD_VAR";
        BashCommand bashCommand = new BashCommand(command, envVars);
        bashCommand.execute();
        bashCommand.addEnvironmentVariable("ADD_VAR", "addedVar");
        CommandResponse response = bashCommand.execute(command2);
        assertTrue(response.isSuccess(), "Command should have returned success");
        assertEquals("addedVar", response.getStdOutLines().get(0), "Command output was not what expected");
    }
}
