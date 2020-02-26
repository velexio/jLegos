package com.velexio.oss.jlegos.util;

import java.text.ChoiceFormat;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Date base utilities found common to need in software projects.
 * <p>
 * The default date format is <b>"yyyy.MM.dd HH:mm:ss"</b>.
 * </p>
 * Example: 1970.01.01 00:00:00 to represent January 01, 1970
 */
public class DateUtils {


    private static final String DEFAULT_FORMAT_PATTERN = "yyyy.MM.dd HH:mm:ss";

    /**
     * Use to get the nowTime formatted as a string method to specify custom format
     *
     * @return a string of nowTime using the default format
     */
    public static String getNow() {
        return DateUtils.formatMillis(new Date().getTime());
    }

    public static String getNow(String pattern) {
        return DateUtils.formatMillis(new Date().getTime(), pattern);
    }

    /**
     * Returns a string in the format "1970.01.01 00:00:00" for the millisecond representation for the
     * Date of February 23, 1975 at 00:00:00 AM.  If you want a different format, use the overloaded
     * method that support supplying the correct format.
     *
     * @param timeAsMillis the time represented in milliseconds
     * @return A string formatted with the default format
     */
    public static String formatMillis(long timeAsMillis) {
        return DateUtils.buildFormat(DEFAULT_FORMAT_PATTERN).format(DateUtils.getDateFromMillis(timeAsMillis));
    }

    /**
     * Returns a formatted string representing the date specified with provided format pattern.
     *
     * @param timeAsMillis  the time represented in milliseconds
     * @param formatPattern a valid date format pattern that can be supplied to {@link java.text.SimpleDateFormat}
     * @return A string formatted with the passed in format pattern
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html">SimpleDateFormat JavaDoc</a>
     */
    public static String formatMillis(long timeAsMillis, String formatPattern) {
        return DateUtils.buildFormat(formatPattern).format(DateUtils.getDateFromMillis(timeAsMillis));
    }

    public static String dynamicElapsedTime(long elapsedMillis) {
        double[] limits = {1, 2};
        Map<String, String[]> unitFormatMap = Map.of(
                "s", new String[]{" second", " seconds"},
                "m", new String[]{" minute", "minutes"},
                "h", new String[]{" hour", " hours"},
                "d", new String[]{" day", " days"}
        );
        ArrayList<String> returnValues = new ArrayList<>();

        long day = TimeUnit.MILLISECONDS.toDays(elapsedMillis);
        long hour = TimeUnit.MILLISECONDS.toHours(elapsedMillis)
                - TimeUnit.MILLISECONDS.toHours(TimeUnit.MILLISECONDS.toDays(elapsedMillis));
        long min = TimeUnit.MILLISECONDS.toMinutes(elapsedMillis)
                - TimeUnit.MILLISECONDS.toMinutes(TimeUnit.MILLISECONDS.toHours(elapsedMillis));
        long sec = TimeUnit.MILLISECONDS.toSeconds(elapsedMillis)
                - TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsedMillis));

        if (day > 0) {
            ChoiceFormat f = new ChoiceFormat(limits, unitFormatMap.get("d"));
            returnValues.add(day + f.format(day));
        }

        if (hour > 0) {
            ChoiceFormat f = new ChoiceFormat(limits, unitFormatMap.get("h"));
            returnValues.add(hour + f.format(hour));
        }

        if (min > 0) {
            ChoiceFormat f = new ChoiceFormat(limits, unitFormatMap.get("m"));
            returnValues.add(min + f.format(min));
        }

        if (sec > 0) {
            ChoiceFormat f = new ChoiceFormat(limits, unitFormatMap.get("s"));
            returnValues.add(sec + f.format(sec));
        }

        return String.join(", ", returnValues);

    }


    private static Date getDateFromMillis(long m) {
        return new Date(m);
    }

    private static DateFormat buildFormat(String formatPattern) {
        return new SimpleDateFormat(formatPattern);
    }

}
