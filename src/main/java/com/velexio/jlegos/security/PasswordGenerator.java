package com.velexio.jlegos.security;

import com.velexio.jlegos.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * <pre>
 * Password generator class to generate good random passwords of specified length.
 *
 * Implements the Builder Pattern to first get an instance with desired options. You
 * can then use the instance to generate a password of any given length
 *
 * The default options are set for a password are:
 *          useLower: true
 *          useUpper: true
 *         useDigits: true
 *        useSpecial: false
 *      excludedList: empty
 *
 * The excludedList is a list of characters that should not be used
 * in the generation of the password
 *
 * Usage Examples:
 *
 *     Example 1 - Default options
 *
 *     PasswordGenerator generator = new PasswordGenerator.Builder.build();
 *     String password = generator.generate(10);
 *
 *     This example creates a password with following attributes:
 *        - has lowercase english letters
 *        - has uppercase english letters
 *        - has numbers (digits)
 *        - does not have any special characters
 *        - has a length of 10 total characters
 *
 *     Example 2 - With exclusions <br><br>
 *
 *      {@code List<String> excludedChars = new ArrayList<>(List.of("@", "[", "]")); }
 *      PasswordGenerator generator = new PasswordGenerator.Builder()
 *               .useSpecial(true)
 *               .useDigits(false)
 *               .addExcludeList(excludedChars)
 *               .build();
 *       String pass = generator.generate(12);
 *
 *     This example creates a password with following attributes:
 *        - has lowercase english letters
 *        - has uppercase english letters
 *        - has no numbers (digits)
 *        - has special characters
 *        - does NOT have @, [, or ] in the password
 *        - has a length of 12 total characters
 *   </pre>
 */
public class PasswordGenerator {

    private boolean useLower;
    private boolean useUpper;
    private boolean useDigits;
    private boolean useSpecial;
    private List<String> excludedList;

    private PasswordGenerator() {
        throw new UnsupportedOperationException("Must use Builder to create class instance");
    }

    private PasswordGenerator(Builder builder) {
        this.useLower = builder.useLower;
        this.useUpper = builder.useUpper;
        this.useDigits = builder.useDigits;
        this.useSpecial = builder.useSpecial;
        this.excludedList = builder.excludedList;
    }

    /**
     * Generates password with options provided to PasswordGenerator.Builder
     *
     * @param passwordLength Length of characters the password should have
     * @return A password meeting policy/criteria
     */
    public String generate(int passwordLength) {
        if (passwordLength <= 0) {
            return "";
        }

        StringBuilder password = new StringBuilder(passwordLength);
        Random random = new Random(System.nanoTime());
        List<String> charOptions = new ArrayList<>(4);

        if (useLower) {
            charOptions.add(StringUtil.LOWER_ENGLISH_ALPHA);
        }
        if (useUpper) {
            charOptions.add(StringUtil.UPPER_ENGLISH_ALPHA);
        }
        if (useDigits) {
            charOptions.add(StringUtil.NUMBERS);
        }
        if (useSpecial) {
            charOptions.add(StringUtil.SPECIAL_CHARS);
        }

        while (password.length() < passwordLength) {
            String option = charOptions.get(random.nextInt(charOptions.size()));
            int randIdx = random.nextInt(option.length());
            char c = option.charAt(randIdx);
            if (!excludedList.contains(String.valueOf(c))) {
                password.append(c);
            }
        }
        return new String(password);
    }

    /**
     * Builder for creating instance with specified options
     */
    public static class Builder {
        private boolean useLower;
        private boolean useUpper;
        private boolean useDigits;
        private boolean useSpecial;
        private List<String> excludedList;

        public Builder() {
            this.useLower = true;
            this.useUpper = true;
            this.useDigits = true;
            this.useSpecial = false;
            this.excludedList = new ArrayList<>();
        }

        /**
         * Controls whether lowercase letters are to be used in password
         * Default: true
         *
         * @param useLower boolean to indicate that password should have lowercase
         * @return Builder
         */
        public Builder useLower(boolean useLower) {
            this.useLower = useLower;
            return this;
        }

        /**
         * Controls whether uppercase letters are to be used in password
         * Default: true
         *
         * @param useUpper boolean to change uppercase setting
         * @return Builder
         */
        public Builder useUpper(boolean useUpper) {
            this.useUpper = useUpper;
            return this;
        }

        /**
         * Controls whether numbers are to be used in password
         * Default: true
         *
         * @param useDigits Set explicit user of digits in password
         * @return Builder
         */
        public Builder useDigits(boolean useDigits) {
            this.useDigits = useDigits;
            return this;
        }

        /**
         * Controls whether special characters are to be used in password
         * Default: false
         *
         * @param useSpecial explicit setting of useage of special characters
         * @return Builder
         */
        public Builder useSpecial(boolean useSpecial) {
            this.useSpecial = useSpecial;
            return this;
        }

        /**
         * Use this builder option to add an list of excluded characters
         *
         * @param excludedList List of excluded characters
         * @return Builder
         */
        public Builder addExcludeList(List<String> excludedList) {
            this.excludedList = excludedList;
            return this;
        }

        /**
         * Builds the PasswordGenerator instance with default and/or specified options
         *
         * @return Instance of PasswordGenerator
         */
        public PasswordGenerator build() {
            return new PasswordGenerator(this);
        }

    }
}
