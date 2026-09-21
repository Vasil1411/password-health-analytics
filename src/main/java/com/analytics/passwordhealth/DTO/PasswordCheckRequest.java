package com.analytics.passwordhealth.DTO;


import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


@Data
public class PasswordCheckRequest {
    @NotBlank(message = "Паролата не може да бъде празна или да съдържа само интервали!")
    @Size(min = 8, max = 128, message = "Паролата трябва да бъде между 8 и 128 символа!")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Паролата трябва да съдържа поне една малка буква, една главна буква, една цифра и един специален символ (@$!%*?&)!"
    )
    private String password;
}