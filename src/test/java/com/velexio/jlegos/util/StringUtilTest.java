package com.velexio.jlegos.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringUtilTest {

    @Test
    void hasDigitsWorks() {
        assertTrue(StringUtil.hasDigits("String234"), "Number present, should be true");
    }

    @Test
    void hasDigitNoDigitWorks() {
        assertFalse(StringUtil.hasDigits("String"), "No Number present, should have been false");
    }

    @Test
    void hasSpecialTrueWorks() {
        assertTrue(StringUtil.hasSpecial("String#1"), "Special not detected, should be true");
    }

    @Test
    void hasSpecialFalseWorks() {
        assertFalse(StringUtil.hasSpecial("String1"), "Special not present, should be false");
    }

    @Test
    void hasCharPositiveWorks() {
        assertTrue(StringUtil.hasChar("String", 'r'),
                "Did not detect char that was present");
    }

    @Test
    void hasCharFalseWorks() {
        assertFalse(StringUtil.hasChar("String", 'p'),
                "Detected char that was NOT present");
    }

    @Test
    void hasCharSpecialsPositiveWorks() {
        assertTrue(StringUtil.hasChar(StringUtil.SPECIAL_CHARS, '@'),
                "Did not detect char that was present");
    }

    @Test
    void hasCharSpecialsFalseWorks() {
        assertFalse(StringUtil.hasChar(StringUtil.SPECIAL_CHARS, 'Z'),
                "Detected char that was NOT present");
    }

    @Test
    void hasCharsPositiveWorks() {
        char[] chars = {'o', 'a'};
        assertTrue(StringUtil.hasChars("FooBar", chars), "Did NOT detect chars from valid list");
    }

    @Test
    void hasCharsFalseWorks() {
        char[] chars = {'T', 'x'};
        assertFalse(StringUtil.hasChars("FooBar", chars), "Detected chars NOT on valid list");
    }

    @Test
    void hasCharsCaseSensitiveWorks() {
        char[] chars = {'b', 'f'};
        assertFalse(StringUtil.hasChars("FooBar", chars), "Detected chars NOT on valid list");
    }

}
