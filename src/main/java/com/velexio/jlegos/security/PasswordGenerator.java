package com.velexio.jlegos.security;

import com.velexio.jlegos.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


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
            if (!excludedList.contains(c)) {
                password.append(c);
            }
        }
        return new String(password);
    }

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

        public Builder useLower(boolean useLower) {
            this.useLower = useLower;
            return this;
        }

        public Builder useUpper(boolean useUpper) {
            this.useUpper = useUpper;
            return this;
        }

        public Builder useDigits(boolean useDigits) {
            this.useDigits = useDigits;
            return this;
        }

        public Builder useSpecial(boolean useSpecial) {
            this.useSpecial = useSpecial;
            return this;
        }

        public Builder addExcludeList(List<String> excludedList) {
            this.excludedList = excludedList;
            return this;
        }

        public PasswordGenerator build() {
            return new PasswordGenerator(this);
        }

    }
}
