package com.analytics.passwordhealth.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
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

    public long checkBreachCount(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }

        String sha1Hash = hashSha1(password).toUpperCase();
        String prefix = sha1Hash.substring(0, 5);
        String suffix = sha1Hash.substring(5);

        try {
            // Добавен е timeout от 2 секунди
            String response = webClient.get()
                    .uri("/range/{prefix}", prefix)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(2)) // <-- Прекратява бързо при липса на интернет
                    .onErrorReturn("") // Връща празен отговор при таймаут/мрежова грешка
                    .block();

            if (response == null || response.isEmpty()) {
                return 0;
            }

            for (String line : response.split("\r?\n")) {
                String[] parts = line.split(":");
                if (parts.length == 2 && parts[0].equalsIgnoreCase(suffix)) {
                    return Long.parseLong(parts[1].trim());
                }
            }
        } catch (Exception e) {
            System.err.println("HIBP API unavailable (offline mode): " + e.getMessage());
        }

        return 0;
    }

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