package com.velexio.jlegos.util;

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
@SuppressWarnings("IntegerDivisionInFloatingPointContext")
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

    /**
     * Will return the amount of elapsed time (represented in milliseconds) to a human readable format of
     * days, hours, minutes, seconds
     *
     * Example:
     *   10 days, 4 hours, 1 minute, 45 seconds
     *   1 day, 1 hour, 2 minutes, 30 seconds
     *   2 minutes, 10 seconds
     *
     * @param elapsedMillis A long, repersenting the number of milliseconds of the elapsed time.
     * @return A string representing a human readable format.
     */
    public static String getFormattedElapsedTime(long elapsedMillis) {
        double[] limits = {1, 2};
        Map<String, String[]> unitFormatMap = Map.of(
                "s", new String[]{" second", " seconds"},
                "m", new String[]{" minute", " minutes"},
                "h", new String[]{" hour", " hours"},
                "d", new String[]{" day", " days"},
                "w", new String[]{" week", " weeks"},
                "mo", new String[]{" month", " months"},
                "y", new String[]{" year", " years"}
        );
        ArrayList<String> returnValues = new ArrayList<>();

        long year = Math.round(TimeUnit.MILLISECONDS.toDays(elapsedMillis) / 365);
        long month = Math.round(TimeUnit.MILLISECONDS.toDays(elapsedMillis) / 30)
                - yearsToMonths(year);
        long week = Math.round(TimeUnit.MILLISECONDS.toDays(elapsedMillis) / 7)
                - monthsToWeeks(yearsToMonths(year))
                - monthsToWeeks(month);
        long day = TimeUnit.MILLISECONDS.toDays(elapsedMillis)
                - yearsToDays(year)
                - monthsToDays(month)
                - weeksToDays(week);
        long hour = TimeUnit.MILLISECONDS.toHours(elapsedMillis)
                - TimeUnit.DAYS.toHours(yearsToDays(year))
                - TimeUnit.DAYS.toHours(monthsToDays(month))
                - TimeUnit.DAYS.toHours(weeksToDays(week))
                - TimeUnit.DAYS.toHours(day);
        long min = TimeUnit.MILLISECONDS.toMinutes(elapsedMillis)
                - TimeUnit.DAYS.toMinutes(yearsToDays(year))
                - TimeUnit.DAYS.toMinutes(monthsToDays(month))
                - TimeUnit.DAYS.toMinutes(weeksToDays(week))
                - TimeUnit.DAYS.toMinutes(day)
                - TimeUnit.HOURS.toMinutes(hour);
        long sec = TimeUnit.MILLISECONDS.toSeconds(elapsedMillis)
                - TimeUnit.DAYS.toSeconds(yearsToDays(year))
                - TimeUnit.DAYS.toSeconds(monthsToDays(month))
                - TimeUnit.DAYS.toSeconds(weeksToDays(week))
                - TimeUnit.DAYS.toSeconds(day)
                - TimeUnit.HOURS.toSeconds(hour)
                - TimeUnit.MINUTES.toSeconds(min);

        if (year > 0) {
            ChoiceFormat f = new ChoiceFormat(limits, unitFormatMap.get("y"));
            returnValues.add("~ "+ year + f.format(year));
        }

        if (month > 0) {
            ChoiceFormat f = new ChoiceFormat(limits, unitFormatMap.get("mo"));
            if (year < 1) {
                returnValues.add("~ " + month + f.format(month));
            } else {
                returnValues.add(month + f.format(month));
            }
        }

        if (week > 0) {
            ChoiceFormat f = new ChoiceFormat(limits, unitFormatMap.get("w"));
            returnValues.add(week + f.format(week));
        }

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

        if (returnValues.size() < 1) {
            return "Under a second";
        } else {
            return String.join(", ", returnValues);
        }

    }

    private static long weeksToDays(long weeks) {
        return weeks * 7;
    }

    private static long monthsToWeeks(long months) {
//        return Math.round(months * 4.34524);
        return (long) Math.floor(months * 4.34524);
    }

    private static long monthsToDays(long months) {
        return months * 30;
    }

    private static long yearsToMonths(long years) {
        return years * 12;
    }

    private static long yearsToDays(long years) {
        return Math.round(years * 365.25);
    }


    private static Date getDateFromMillis(long m) {
        return new Date(m);
    }

    private static DateFormat buildFormat(String formatPattern) {
        return new SimpleDateFormat(formatPattern);
    }

}
