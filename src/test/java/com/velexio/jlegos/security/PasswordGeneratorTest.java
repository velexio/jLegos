package com.velexio.jlegos.security;

import com.velexio.jlegos.util.StringUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordGeneratorTest {

    PasswordGenerator defaultGenerator = new PasswordGenerator.Builder().build();

    @Test
    void generateLengthWorks() {
        PasswordGenerator generator = new PasswordGenerator.Builder().build();
        String p = generator.generate(8);
        assertEquals(8, p.length(), "Password was not expected length");
    }

    @Test
    void testNumberInDefault() {
        String p = defaultGenerator.generate(10);
        assertTrue(StringUtil.hasDigits(p));
    }
}
