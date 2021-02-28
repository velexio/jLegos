package com.velexio.jlegos.util;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateUtilsTest {
    Calendar baseCal = new GregorianCalendar(2008, Calendar.SEPTEMBER, 1, 0, 0, 0);
    final Date baseDate = baseCal.getTime();
    private long elapsed = 0;

    @Test
    void testDefaultFormatWorks() {
        String expFormat
                = "2008.09.01 00:00:00";
        assertEquals(
                expFormat, DateUtils.formatMillis(baseDate.getTime()));
    }

    @Test
    void testFormatWithCustomFormat() {
        String cf01 = "2008.Sep.01 00:00:00";
        String cf02 = "2008-Sep-01 00:00:00";
        assertEquals(cf01, DateUtils.formatMillis(baseDate.getTime(), "yyyy.MMM.dd HH:mm:ss"));
        assertEquals(cf02, DateUtils.formatMillis(baseDate.getTime(), "yyyy-MMM-dd HH:mm:ss"));
    }

    @Test
    void testTimestamp() throws ParseException {
        Calendar cal = new GregorianCalendar(2020, Calendar.JANUARY, 1, 8, 10, 30);
        String oraTimestamp = "2020-01-01 08:10:30.345";
        String converted = "2020-01-01 08:10:30.345";
        Date date = DateUtils.dateFromString(oraTimestamp, "yyyy-MM-dd HH:mm:ss.SSS");
        String dateConverted = DateUtils.formatMillis(date.getTime(), "yyyy-MM-dd HH:mm:ss.SSS");
        assertEquals(converted, dateConverted);

    }

    @Test
    void testDateFromStringWorks() throws ParseException {
        Calendar cal = new GregorianCalendar(2008, Calendar.SEPTEMBER, 1, 0, 1, 30);
        Date baseDate = cal.getTime();
        String tc1 = "2008.09.01 00:01:30";
        assertEquals(baseDate, DateUtils.dateFromString(tc1));
        String tc2 = "2008.09.01 00:01:30.515";
        cal.add(Calendar.MILLISECOND, 515);
        baseDate = cal.getTime();
        assertEquals(baseDate, DateUtils.dateFromString(tc2, "yyyy.MM.dd HH:mm:ss.SSS"));
        assertEquals(tc2, DateUtils.formatMillis(baseDate.getTime(), "yyyy.MM.dd HH:mm:ss.SSS"));
        String tc3 = "2019.10.24 12:46:19.515";
        Calendar cal3 = new GregorianCalendar(2019, Calendar.OCTOBER, 24, 12, 46, 19);
        cal3.add(Calendar.MILLISECOND, 515);
        Date date3 = cal3.getTime();
        assertEquals(date3, DateUtils.dateFromString(tc3, "yyyy.MM.dd HH:mm:ss.SSS"));

    }

    @Test
    void elapsedFormattedSecondsDisplayWorks() {
        Calendar end1 = new GregorianCalendar(2008, Calendar.SEPTEMBER, 1, 0, 0, 0);
        elapsed = end1.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("Under a second", DateUtils.getFormattedElapsedTime(elapsed));
        Calendar end2 = new GregorianCalendar(2008, Calendar.SEPTEMBER, 1, 0, 0, 1);
        elapsed = end2.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("1 second", DateUtils.getFormattedElapsedTime(elapsed));
        Calendar end3 = new GregorianCalendar(2008, Calendar.SEPTEMBER, 1, 0, 0, 45);
        elapsed = end3.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("45 seconds", DateUtils.getFormattedElapsedTime(elapsed));
    }

    @Test
    void elapsedFormattedMinutesDisplayWorks() {
        Calendar end1 = new GregorianCalendar(2008, Calendar.SEPTEMBER, 1, 0, 1, 0);
        elapsed = end1.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("1 minute", DateUtils.getFormattedElapsedTime(elapsed));
        Calendar end2 = new GregorianCalendar(2008, Calendar.SEPTEMBER, 1, 0, 1, 1);
        elapsed = end2.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("1 minute, 1 second", DateUtils.getFormattedElapsedTime(elapsed));
        Calendar end3 = new GregorianCalendar(2008, Calendar.SEPTEMBER, 1, 0, 2, 30);
        elapsed = end3.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("2 minutes, 30 seconds", DateUtils.getFormattedElapsedTime(elapsed));
        Calendar end4 = new GregorianCalendar(2008, Calendar.SEPTEMBER, 1, 0, 5, 0);
        elapsed = end4.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("5 minutes", DateUtils.getFormattedElapsedTime(elapsed));
    }

    @Test
    void elapsedFormattedHoursDisplayWorks() {
        Calendar end1 = new GregorianCalendar(2008, Calendar.SEPTEMBER, 1, 1, 0, 0);
        elapsed = end1.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("1 hour", DateUtils.getFormattedElapsedTime(elapsed));
        Calendar end2 = new GregorianCalendar(2008, Calendar.SEPTEMBER, 1, 2, 1, 0);
        elapsed = end2.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("2 hours, 1 minute", DateUtils.getFormattedElapsedTime(elapsed));
        Calendar end3 = new GregorianCalendar(2008, Calendar.SEPTEMBER, 1, 2, 1, 30);
        elapsed = end3.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("2 hours, 1 minute, 30 seconds", DateUtils.getFormattedElapsedTime(elapsed));
        Calendar end4 = new GregorianCalendar(2008, Calendar.SEPTEMBER, 1, 20, 0, 0);
        elapsed = end4.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("20 hours", DateUtils.getFormattedElapsedTime(elapsed));
    }

    @Test
    void elapsedFormattedDayDisplayWorks() {
        Calendar end1 = new GregorianCalendar(2008, Calendar.SEPTEMBER, 2, 0, 0, 0);
        elapsed = end1.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("1 day", DateUtils.getFormattedElapsedTime(elapsed));
        Calendar end2 = new GregorianCalendar(2008, Calendar.SEPTEMBER, 3, 0, 0, 0);
        elapsed = end2.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("2 days", DateUtils.getFormattedElapsedTime(elapsed));
        Calendar end3 = new GregorianCalendar(2008, Calendar.SEPTEMBER, 3, 4, 0, 0);
        elapsed = end3.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("2 days, 4 hours", DateUtils.getFormattedElapsedTime(elapsed));
        Calendar end4 = new GregorianCalendar(2008, Calendar.SEPTEMBER, 3, 4, 31, 10);
        elapsed = end4.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("2 days, 4 hours, 31 minutes, 10 seconds", DateUtils.getFormattedElapsedTime(elapsed));
        Calendar end5 = new GregorianCalendar(2008, Calendar.SEPTEMBER, 7, 4, 31, 10);
        elapsed = end5.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("6 days, 4 hours, 31 minutes, 10 seconds", DateUtils.getFormattedElapsedTime(elapsed));
    }

    @Test
    void elapsedFormattedWeekDisplayWorks() {
        Calendar end1 = new GregorianCalendar(2008, Calendar.SEPTEMBER, 8, 0, 0, 0);
        elapsed = end1.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("1 week", DateUtils.getFormattedElapsedTime(elapsed));
        Calendar end2 = new GregorianCalendar(2008, Calendar.SEPTEMBER, 9, 0, 0, 0);
        elapsed = end2.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("1 week, 1 day", DateUtils.getFormattedElapsedTime(elapsed));
        Calendar end3 = new GregorianCalendar(2008, Calendar.SEPTEMBER, 9, 2, 0, 0);
        elapsed = end3.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("1 week, 1 day, 2 hours", DateUtils.getFormattedElapsedTime(elapsed));
        Calendar end4 = new GregorianCalendar(2008, Calendar.SEPTEMBER, 11, 2, 0, 10);
        elapsed = end4.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("1 week, 3 days, 2 hours, 10 seconds", DateUtils.getFormattedElapsedTime(elapsed));
        Calendar end5 = new GregorianCalendar(2008, Calendar.SEPTEMBER, 20, 2, 1, 10);
        elapsed = end5.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("2 weeks, 5 days, 2 hours, 1 minute, 10 seconds", DateUtils.getFormattedElapsedTime(elapsed));
    }

    @Test
    void elapsedFormattedMonthDisplayWorks() {
        Calendar end1 = new GregorianCalendar(2008, Calendar.OCTOBER, 1, 0, 0, 0);
        elapsed = end1.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("~ 1 month", DateUtils.getFormattedElapsedTime(elapsed));
        Calendar end2 = new GregorianCalendar(2008, Calendar.OCTOBER, 5, 0, 0, 0);
        elapsed = end2.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("~ 1 month, 4 days", DateUtils.getFormattedElapsedTime(elapsed));
        Calendar end3 = new GregorianCalendar(2008, Calendar.OCTOBER, 5, 0, 30, 0);
        elapsed = end3.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("~ 1 month, 4 days, 30 minutes", DateUtils.getFormattedElapsedTime(elapsed));
        Calendar end4 = new GregorianCalendar(2008, Calendar.NOVEMBER, 1, 0, 30, 0);
        elapsed = end4.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("~ 2 months, 1 day, 30 minutes", DateUtils.getFormattedElapsedTime(elapsed));
    }

    @Test
    void elapsedFormattedYearDisplayWorks() {
        Calendar end1 = new GregorianCalendar(2009, Calendar.SEPTEMBER, 1, 0, 0, 0);
        elapsed = end1.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("~ 1 year", DateUtils.getFormattedElapsedTime(elapsed));
        Calendar end2 = new GregorianCalendar(2010, Calendar.SEPTEMBER, 1, 0, 0, 0);
        elapsed = end2.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("~ 2 years", DateUtils.getFormattedElapsedTime(elapsed));
    }

    @Test
    void addDaysWorks() {
        Date end1 = new GregorianCalendar(2008, Calendar.SEPTEMBER, 2, 0, 0, 0).getTime();
        assertEquals(end1, DateUtils.addDays(baseCal.getTime(), 1));
    }

    @Test
    void addHoursWorks() {
        Date end1 = new GregorianCalendar(2008, Calendar.SEPTEMBER, 1, 4, 0, 0).getTime();
        assertEquals(end1, DateUtils.addHours(baseCal.getTime(), 4));
    }

    @Test
    void diffToDaysWorks() {
        Date end = new GregorianCalendar(2008, Calendar.SEPTEMBER, 2, 0, 0, 0).getTime();
        assertEquals(1, DateUtils.diffToDays(baseCal.getTime(), end));
        end = new GregorianCalendar(2008, Calendar.SEPTEMBER, 3, 0, 0, 0).getTime();
        double diff = DateUtils.diffToDays(baseCal.getTime(), end);
        assertEquals(2, diff);
        end = new GregorianCalendar(2008, Calendar.SEPTEMBER, 3, 23, 0, 0).getTime();
        assertEquals(2, DateUtils.diffToDays(baseCal.getTime(), end));
    }

    @Test
    void diffToHoursWorks() {
        Date end = new GregorianCalendar(2008, Calendar.SEPTEMBER, 1, 12, 0, 0).getTime();
        assertEquals(12, DateUtils.diffToHours(baseCal.getTime(), end));
        end = new GregorianCalendar(2008, Calendar.SEPTEMBER, 2, 4, 0, 0).getTime();
        assertEquals(28, DateUtils.diffToHours(baseCal.getTime(), end));
    }

}
