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

}
