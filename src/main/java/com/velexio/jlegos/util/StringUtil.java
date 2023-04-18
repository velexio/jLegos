package com.velexio.jlegos.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handy functions to use with Strings
 */
public class StringUtil {
    /**
     * All valid English lowercase alphabet characters
     */
    public static final String LOWER_ENGLISH_ALPHA = "abcdefghijklmnopqrstuvwxyz";
    /**
     * All valid English uppercase alphabet characters
     */
    public static final String UPPER_ENGLISH_ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /**
     * The list of valid numbers
     */
    public static final String NUMBERS = "0123456789";
    /**
     * Default special characters
     */
    public static final String SPECIAL_CHARS = "!@#$%&*()_+-=[]|,./?><";

    public static boolean hasDigits(String str) {
        if (StringUtil.hasValue(str)) {
            Pattern matchPattern = Pattern.compile("[0-9]");
            Matcher matcher = matchPattern.matcher(str);
            return matcher.find();
        }
        return false;
    }

    /**
     * Boolean check to see if string has any special characters
     *
     * @param str String value to check
     * @return true if special characters are in the string
     */
    public static boolean hasSpecial(String str) {
        if (StringUtil.hasValue(str)) {
            boolean found = false;
            List<String> specialAsList = asList(SPECIAL_CHARS);
            char[] checkChars = str.toCharArray();
            for (char c : checkChars) {
                if (specialAsList.contains(Character.toString(c))) {
                    found = true;
                    break;
                }
            }
            return found;
        }
        return false;
    }

    /**
     * Determines if a String value has a specified character in it
     *
     * @param str         The string to check for character
     * @param compareChar the character to search for
     * @return {@code true} if the passed in string contains the has specified char
     */
    public static boolean hasChar(String str, char compareChar) {
        if (StringUtil.hasValue(str)) {
            char[] stringCharArray = str.toCharArray();
            for (char c : stringCharArray) {
                if (c == compareChar) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks the string for an characters found in passed in array
     *
     * @param str the string to check
     * @param compareArray The array of characters to search
     * @return true if any of the charcters are found
     */
    public static boolean hasChars(String str, char[] compareArray) {
        if (StringUtil.hasValue(str)) {
            Set<String> stringSet = new HashSet<>();
            Set<String> compareSet = new HashSet<>();

            for (char c : str.toCharArray()) {
                stringSet.add(String.valueOf(c));
            }

            for (char c : compareArray) {
                compareSet.add(String.valueOf(c));
            }

            stringSet.retainAll(compareSet);
            return stringSet.size() > 0;
        }
        return false;
    }

    /**
     * Will return the string as a list of all the individual characters that compose the string
     *
     * @param str the string to convert
     * @return List of all characters in the string
     */
    public static List<String> asList(String str) {
        if (StringUtil.hasValue(str)) {
            List<String> stringList = new ArrayList<>(str.length());
            char[] stringChars = str.toCharArray();
            for (char c : stringChars) {
                stringList.add(Character.toString(c));
            }
            return stringList;
        }
        return new ArrayList<>();
    }


    /**
     * Checks that string has an actual value, not null and not just an empty string.
     *
     * @param str The string value to check
     * @return {@code true} if the {@code String} is non-null and is NOT and empty string
     */
    public static boolean hasValue(String str) {
        return (str != null && str.length() > 0);
    }


}
