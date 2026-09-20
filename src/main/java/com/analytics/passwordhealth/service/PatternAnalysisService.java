package com.analytics.passwordhealth.service;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class PatternAnalysisService {

    // Често срещани клавиатурни шаблони
    private static final List<String> KEYBOARD_PATTERNS = List.of(
            "qwerty", "qwertz", "azerty", "asdfgh", "zxcvbn",
            "12345", "123456", "12345678", "123456789", "0987654321"
    );

    /**
     * Анализира паролата за намерени шаблони и връща списък с предупреждения
     */
    public List<String> analyzePatterns(String password) {
        List<String> warnings = new ArrayList<>();

        if (password == null || password.isEmpty()) {
            return warnings;
        }

        String lowerPass = password.toLowerCase();

        // 1. Проверка за клавиатурни шаблони
        for (String pattern : KEYBOARD_PATTERNS) {
            if (lowerPass.contains(pattern)) {
                warnings.add("Contains common keyboard pattern: '" + pattern + "'");
            }
        }

        // 2. Проверка за повтарящи се символи (3 или повече еднакви последователни: "aaa", "111")
        if (Pattern.compile("(.)\\1{2,}").matcher(password).find()) {
            warnings.add("Contains repeated characters (e.g. 'aaa' or '111')");
        }

        // 3. Проверка за Leetspeak замени (p@ssword, admin123)
        String normalized = normalizeLeetspeak(lowerPass);
        if (!normalized.equals(lowerPass)) {
            for (String pattern : KEYBOARD_PATTERNS) {
                if (normalized.contains(pattern)) {
                    warnings.add("Contains hidden pattern via character substitution (Leetspeak)");
                    break;
                }
            }
        }

        // 4. Проверка за година в края (напр. 1998, 2024, 2026)
        if (Pattern.compile("(19|20)\\d{2}$").matcher(password).find()) {
            warnings.add("Ends with a recent year or birth year pattern");
        }

        return warnings;
    }

    /**
     * Преобразува Leetspeak символи към стандартни букви (напр. @ -> a, 0 -> o)
     */
    private String normalizeLeetspeak(String input) {
        return input.replace('@', 'a')
                .replace('4', 'a')
                .replace('3', 'e')
                .replace('1', 'i')
                .replace('!', 'i')
                .replace('0', 'o')
                .replace('$', 's')
                .replace('5', 's')
                .replace('7', 't');
    }
}