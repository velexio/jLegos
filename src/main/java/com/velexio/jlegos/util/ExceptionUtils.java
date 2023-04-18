package com.velexio.jlegos.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Class to hold handy utilities when dealing with Exceptions
 */
public class ExceptionUtils {

    /**
     * Returns the stack trac as a string object from a Throwable object
     *
     * @param throwable Object that is child of Throwable
     * @return String object that holds full stack trace
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

}
