package com.velexio.jlegos.util;

import org.junit.jupiter.api.Test;

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
    void secondsDisplayWorks() {
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
    void minutesDisplayWorks() {
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
    void hourDisplayWorks() {
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
    void dayDisplayWorks() {
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
    void weekDisplayWorks() {
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
    void monthDisplayWorks() {
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
    void yearDisplayWorks() {
        Calendar end1 = new GregorianCalendar(2009, Calendar.SEPTEMBER, 1, 0, 0, 0);
        elapsed = end1.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("~ 1 year", DateUtils.getFormattedElapsedTime(elapsed));
        Calendar end2 = new GregorianCalendar(2010, Calendar.SEPTEMBER, 1, 0, 0, 0);
        elapsed = end2.getTimeInMillis() - baseCal.getTimeInMillis();
        assertEquals("~ 2 years", DateUtils.getFormattedElapsedTime(elapsed));
    }

}