package com.analytics.passwordhealth.service;

import org.springframework.stereotype.Service;

@Service
public class EntropyCalculatorService {

    /**
     * Изчислява ентропията на парола в битове по формулата: E = L * log2(R)
     * L = Дължина на паролата
     * R = Размер на набора от използвани символи (Character Set Size)
     */
    public double calculateEntropy(String password) {
        if (password == null || password.isEmpty()) {
            return 0.0;
        }

        int poolSize = calculatePoolSize(password);
        int length = password.length();

        if (poolSize == 0) {
            return 0.0;
        }

        // Формула: E = L * (log10(R) / log10(2)) -> преобразуване към логаритъм при основа 2
        double entropy = length * (Math.log(poolSize) / Math.log(2));

        // Закръгляме до 2 знака след запетаята
        return Math.round(entropy * 100.0) / 100.0;
    }

    /**
     * Определя размера на набора от символи (R) спрямо съдържанието
     */
    private int calculatePoolSize(String password) {
        boolean hasLowercase = false;
        boolean hasUppercase = false;
        boolean hasDigits = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isDigit(c)) {
                hasDigits = true;
            } else {
                hasSpecial = true;
            }
        }

        int poolSize = 0;
        if (hasLowercase) poolSize += 26; // a-z
        if (hasUppercase) poolSize += 26; // A-Z
        if (hasDigits) poolSize += 10;    // 0-9
        if (hasSpecial) poolSize += 32;   // спец. символи (ASCII punctuation)

        return poolSize;
    }

    /**
     * Преценява приблизителното време за разбиване (за визуализация в React)
     */
    public String estimateCrackTime(double entropy) {
        if (entropy < 28) return "Instantly (Very Weak)";
        if (entropy < 36) return "Few seconds to minutes";
        if (entropy < 60) return "Few hours to days";
        if (entropy < 80) return "Several months to years";
        return "Centuries (Extremely Strong)";
    }
}