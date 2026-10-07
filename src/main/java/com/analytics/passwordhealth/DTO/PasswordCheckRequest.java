package com.analytics.passwordhealth.DTO;


import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


@Data
public class PasswordCheckRequest {
    @Size(min = 8, max = 128, message = "Password length must be between 8 and 128 characters")
    private String password;
}