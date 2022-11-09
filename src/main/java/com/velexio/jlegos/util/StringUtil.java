package com.velexio.jlegos.util;

import java.util.ArrayList;
import java.util.List;
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

    public static List<String> asList(String stringObject) {
        List<String> stringList = new ArrayList<>(stringObject.length());
        char[] stringChars = stringObject.toCharArray();
        for (char c : stringChars) {
            stringList.add(Character.toString(c));
        }
        return stringList;
    }
}
