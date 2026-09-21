package com.analytics.passwordhealth.Controller;

import com.analytics.passwordhealth.DTO.PasswordCheckRequest;
import com.analytics.passwordhealth.DTO.PasswordAnalysisResponse;
import com.analytics.passwordhealth.service.PasswordHealthAggregatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/password")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Позволява заявки от React в бъдеще
public class PasswordAnalysisController {

    private final PasswordHealthAggregatorService aggregatorService;

    @PostMapping("/analyze")
    public PasswordAnalysisResponse analyze(@Valid @RequestBody PasswordCheckRequest request) {
        return aggregatorService.analyzePassword(request.getPassword());
    }
}