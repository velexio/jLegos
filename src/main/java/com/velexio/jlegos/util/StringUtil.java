package com.velexio.jlegos.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static final String LOWER_ENGLISH_ALPHA = "abcdefghijklmnopqrstuvwxyz";
    public static final String UPPER_ENGLISH_ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String NUMBERS = "0123456789";
    public static final String SPECIAL_CHARS = "!@#$%&*()_+-=[]|,./?><";

    public static boolean hasDigits(String stringToCheck) {
        Pattern matchPattern = Pattern.compile("[0-9]");
        Matcher matcher = matchPattern.matcher(stringToCheck);
        return matcher.find();
    }

    public static boolean hasSpecial(String stringToCheck) {
        boolean found = false;
        List<String> specialAsList = asList(SPECIAL_CHARS);
        char[] checkChars = stringToCheck.toCharArray();
        for (char c : checkChars) {
            if (specialAsList.contains(Character.toString(c))) {
                found = true;
                break;
            }
        }
        return found;
    }

    /**
     * Determines if a String value has a specified character in it
     *
     * @param stringToCheck
     * @param compareChar
     * @return
     */
    public static boolean hasChar(String stringToCheck, char compareChar) {
        char[] stringCharArray = stringToCheck.toCharArray();
        for (char c : stringCharArray) {
            if (c == compareChar) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasChars(String stringToCheck, char[] compareArray) {
        Set<String> stringSet = new HashSet<>();
        Set<String> compareSet = new HashSet<>();

        for (char c : stringToCheck.toCharArray()) {
            stringSet.add(String.valueOf(c));
        }

        for (char c : compareArray) {
            compareSet.add(String.valueOf(c));
        }

        stringSet.retainAll(compareSet);
        return stringSet.size() > 0;

    }

    public static List<String> asList(String stringObject) {
        List<String> stringList = new ArrayList<>(stringObject.length());
        char[] stringChars = stringObject.toCharArray();
        for (char c : stringChars) {
            stringList.add(Character.toString(c));
        }
        return stringList;
    }

}
