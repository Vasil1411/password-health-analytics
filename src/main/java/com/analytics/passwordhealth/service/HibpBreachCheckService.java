package com.analytics.passwordhealth.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class HibpBreachCheckService {

    private final WebClient webClient;

    public HibpBreachCheckService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.pwnedpasswords.com")
                .defaultHeader("User-Agent", "Password-Health-Analytics-App")
                .build();
    }

    /**
     * Проверява колко пъти паролата е засичана в течове чрез k-Anonymity
     * Връща брой намерени течове (0 ако е сигурна)
     */
    public long checkBreachCount(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }

        // 1. Хеширане на паролата с SHA-1
        String sha1Hash = hashSha1(password).toUpperCase();

        // 2. Разделяне: Първи 5 символа (Prefix) и останалите 35 (Suffix)
        String prefix = sha1Hash.substring(0, 5);
        String suffix = sha1Hash.substring(5);

        try {
            // 3. Извикване на външното API с САМО първите 5 символа
            String response = webClient.get()
                    .uri("/range/{prefix}", prefix)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // Синхронно изчакване на отговора

            if (response == null) {
                return 0;
            }

            // 4. Локално търсене на суфикса (35 символа) в получения отговор
            for (String line : response.split("\r?\n")) {
                String[] parts = line.split(":");
                if (parts[0].equalsIgnoreCase(suffix)) {
                    return Long.parseLong(parts[1].trim()); // Връща броя съвпадения
                }
            }
        } catch (Exception e) {
            // В случай на грешка в мрежата или API-то
            System.err.println("Error checking HIBP API: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Генерира SHA-1 хеш на текст
     */
    private String hashSha1(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = digest.digest(input.getBytes());
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not found", e);
        }
    }
}