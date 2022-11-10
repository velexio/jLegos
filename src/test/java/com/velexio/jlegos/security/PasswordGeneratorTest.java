package com.velexio.jlegos.security;

import com.velexio.jlegos.util.StringUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testExclusionWorks() {
        List<String> excludedChars = StringUtil.asList(StringUtil.SPECIAL_CHARS);
        PasswordGenerator generator = new PasswordGenerator.Builder()
                .useSpecial(true)
                .useDigits(false)
                .addExcludeList(excludedChars)
                .build();
        String pass = generator.generate(50);
        System.out.println("Generated password: " + pass);
        for (char c : pass.toCharArray()) {
//            assertFalse(excludedChars.contains(new String(c)));
            if (excludedChars.contains(String.valueOf(c))) {
                fail("Found excluded character in password");
            }
        }
    }
}
