package com.velexio.jlegos.operatingsystem;

import lombok.Builder;

import java.util.List;

@Builder
/**
 * Data object that holds the response information from the execution of Command execution results.
 * The getStandardOutput method will return the full lines returned via stdOut
 * The getErrorOutput will return whatever was written to stdErr
 */
public class CommandResponse {
    private boolean success;
    private List<String> stdOutLines;
    private List<String> stdErrLines;

    /**
     * Indicates if command run was success. Value will be true as long as no lines where returned via
     * standard error. NOTE: It is possible that some commands can run "successfully" while still returning
     * lines to stderr.  Because of this, there can be cases where false negatives occur.  Ultimately the
     * determination of whether a command is successful is up to the caller. isSuccess() is merely a
     * convenience method for when executing a trusted command that never produces stderr on a successful execution.
     *
     * @return boolean to indicate if command was a success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns all the lines of output that were written to stdout by the executed command
     *
     * @return List of Strings, each entry corresponds to the line written to stdout
     */
    public List<String> getStdOutLines() {
        return stdOutLines;
    }

    /**
     * Returns all the lines written to stderr during the execution of the command
     *
     * @return List of Strings, each entry corresponds to the line written to stderr
     */
    public List<String> getStdErrLines() {
        return stdErrLines;
    }


}
