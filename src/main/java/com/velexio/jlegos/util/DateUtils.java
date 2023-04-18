package com.velexio.jlegos.util;

import java.text.ChoiceFormat;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
     * Will return the point in time when called in millisecond format as a long.
     *
     * @return long representing the time in milliseconds
     */
    public static long getNow() {
        return new Date().getTime();
    }

    /**
     * Used to get the current time as a Date object
     *
     * @return Date representing current point in time
     */
    public static Date getNowAsDate() {
        return new Date();
    }

    /**
     * Use to convert a date string to a Date object. The default date format is used to parse.  Only use this on strings that you have retrieved from
     * the DateUtils class as it uses the internal format pattern.  Use method {@link com.velexio.jlegos.util.DateUtils#dateFromString(String, String)} for
     * date strings with custom format.
     *
     * @param dateString The data string that needs to be converted
     * @return A Date object that represents the date/time of the passed in string
     * @throws ParseException Will be thrown if the date value does not correspond with the format pattern.
     */
    public static Date dateFromString(String dateString) throws ParseException {
        return new SimpleDateFormat(DEFAULT_FORMAT_PATTERN).parse(dateString);
    }

    /**
     * Will convert a date string to a Date object using the specified pattern.  Always use this method when working with custom date formats.  If you
     * can be assured that the date string was generated by this class (DateUtils), then a convenience method is the
     * {@link com.velexio.jlegos.util.DateUtils#dateFromString(String)} method that uses the default format pattern.
     *
     * @param dateString              A date in string format to be converted
     * @param simpleDateFormatPattern The SimpleDateFormat pattern to use to make the conversion. See java.text.SimpleDateFormat for reference.
     * @return A Date object that is the date specified by the dateString argument
     * @throws ParseException If the SimpleDateFormat used does not match with the dateString parameter's format
     * @see java.text.SimpleDateFormat
     */
    public static Date dateFromString(String dateString, String simpleDateFormatPattern) throws ParseException {
        return new SimpleDateFormat(simpleDateFormatPattern).parse(dateString);
    }

    /**
     * Use to get the nowTime formatted as a string method to specify custom format
     *
     * @return a string of nowTime using the default format
     */
    public static String getNowAsString() {
        return DateUtils.formatMillis(new Date().getTime());
    }

    /**
     * Handy method to get current date as a formatted string
     *
     * @param pattern The pattern to format the date
     * @return The date as a string formatted with provided pattern;
     */
    public static String getNowAsString(String pattern) {
        return DateUtils.formatMillis(new Date().getTime(), pattern);
    }

    /**
     * Use to get the default format pattern that is used for Date to string formatting if none is specified.
     *
     * @return String representing the SimpleDateFormat used by default
     */
    public String getDefaultFormatPattern() {
        return DEFAULT_FORMAT_PATTERN;
    }

    /**
     * Will format a Date to the specified pattern.  Pattern must be a valid SimpleDateFormat pattern.
     *
     * @param date                    The date to format.
     * @param simpleDateFormatPattern Format pattern. Must be valid SimpleDateFormat pattern.
     * @return String in format specified
     */
    public String formatted(Date date, String simpleDateFormatPattern) {
        return DateUtils.formatMillis(date.getTime(), simpleDateFormatPattern);
    }

    /**
     * REturns the date as a formatted string useing the DEFAULT_FORMAT_PATTERN
     *
     * @param date The date to format
     * @return The string in the default format pattern
     */
    public String formatted(Date date) {
        return DateUtils.formatMillis(date.getTime());
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

    /**
     * Will add the specified number of days to an existing Date object and return the new Date
     *
     * @param date      The base date object to add time
     * @param daysToAdd The number of days to add
     * @return A new Date object with the number of days added
     */
    public static Date addDays(Date date, int daysToAdd) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, daysToAdd);
        return cal.getTime();
    }

    /**
     * Will add the specified number of hours to an existing Date object and return the new Date
     *
     * @param date       The base date object to add time
     * @param hoursToAdd The number of hours to add
     * @return A new Date object with the number of hours added
     */
    public static Date addHours(Date date, int hoursToAdd) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hoursToAdd);
        return cal.getTime();
    }

    /**
     * Will add the specified number of minutes to an existing Date object and return the new Date
     *
     * @param date         The base date object to add time
     * @param minutesToAdd The number of minutes to add
     * @return A new Date object with the number of minutes added
     */
    public static Date addMinutes(Date date, int minutesToAdd) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minutesToAdd);
        return cal.getTime();
    }

    /**
     * Will add the specified number of seconds to an existing Date object and return the new Date
     *
     * @param date         The base date object to add time
     * @param secondsToAdd The number of seconds to add
     * @return A new Date object with the number of seconds added
     */
    public static Date addSeconds(Date date, int secondsToAdd) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, secondsToAdd);
        return cal.getTime();
    }

    /**
     * Converts the difference between two date objects to number of milliseconds.
     * <p>
     * <b>Note:</b> The date order is note relevant as the calculation as the return is an absolute value. It is up to the caller to determine if it
     * should be a positive/negative value.
     * </p>
     *
     * @param firstDate  The first date to compare
     * @param secondDate The second date to compare
     * @return A long representing the difference in milliseconds
     */
    public static long diffToMillis(Date firstDate, Date secondDate) {
        return Math.abs(secondDate.getTime() - firstDate.getTime());
    }

    /**
     * Converts the difference between two date objects to number of seconds. The value is always rounded down to the nearest whole value.
     * <p>
     * <b>Note:</b> The date order is note relevant as the calculation as the return is an absolute value. It is up to the caller to determine if it
     * should be a positive/negative value.
     * </p>
     *
     * @param firstDate  The first date to compare
     * @param secondDate The second date to compare
     * @return A long representing the difference in seconds
     */
    public static long diffToSeconds(Date firstDate, Date secondDate) {
        return TimeUnit.SECONDS.convert(DateUtils.diffToMillis(firstDate, secondDate), TimeUnit.MILLISECONDS);
    }

    /**
     * Converts the difference between two date objects to number of minutes. The value is always rounded down to the nearest whole value.
     * <p>
     * <b>Note:</b> The date order is note relevant as the calculation as the return is an absolute value. It is up to the caller to determine if it
     * should be a positive/negative value.
     * </p>
     *
     * @param firstDate  The first date to compare
     * @param secondDate The second date to compare
     * @return A long representing the difference in minutes
     */
    public static long diffToMinutes(Date firstDate, Date secondDate) {
        return TimeUnit.MINUTES.convert(DateUtils.diffToMillis(firstDate, secondDate), TimeUnit.MILLISECONDS);
    }

    /**
     * Converts the difference between two date objects to number of hours. The value is always rounded down to the nearest whole value.
     * <p>
     * <b>Note:</b> The date order is note relevant as the calculation as the return is an absolute value. It is up to the caller to determine if it
     * should be a positive/negative value.
     * </p>
     *
     * @param firstDate  The first date to compare
     * @param secondDate The second date to compare
     * @return A long representing the difference in hours
     */
    public static long diffToHours(Date firstDate, Date secondDate) {
        return TimeUnit.HOURS.convert(DateUtils.diffToMillis(firstDate, secondDate), TimeUnit.MILLISECONDS);
    }

    /**
     * Converts the difference between to dates to number of days. The value is always rounded down to the nearest whole value.
     * <p>
     * <b>Note:</b> The date order is note relevant as the calculation as the return is an absolute value. It is up to the caller to determine if it
     * should be a positive/negative value.
     * </p>
     *
     * @param firstDate  The first date to compare
     * @param secondDate The second date to compare
     * @return A long representing the difference in days
     */
    public static long diffToDays(Date firstDate, Date secondDate) {
        return TimeUnit.DAYS.convert(DateUtils.diffToMillis(firstDate, secondDate), TimeUnit.MILLISECONDS);
    }

    private static long weeksToDays(long weeks) {
        return weeks * 7;
    }

    private static long monthsToWeeks(long months) {
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
