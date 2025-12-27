package com.tta.dientu.store.areas.user.controller;

import com.tta.dientu.store.service.TtaMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TtaEmailTestController {

    private final TtaMailService mailService;

    @GetMapping("/api/test/send-email")
    public Map<String, Object> testEmail(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();

        try {
            mailService.sendResetPasswordEmail(email, "http://localhost:8080/test-link");
            response.put("success", true);
            response.put("message", "Email sent successfully to: " + email);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());

            // Get root cause
            Throwable cause = e.getCause();
            if (cause != null) {
                response.put("rootCause", cause.getMessage());
                response.put("rootCauseType", cause.getClass().getSimpleName());
            }
        }

        return response;
    }
}
