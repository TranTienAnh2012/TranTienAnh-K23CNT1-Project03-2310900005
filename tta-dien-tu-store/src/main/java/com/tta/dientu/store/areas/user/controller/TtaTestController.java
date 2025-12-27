package com.tta.dientu.store.areas.user.controller;

import com.tta.dientu.store.entity.TtaQuanTriVien;
import com.tta.dientu.store.repository.TtaQuanTriVienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TtaTestController {

    private final TtaQuanTriVienRepository ttaQuanTriVienRepository;

    /**
     * TEST ENDPOINT - Xem token reset password
     * Chỉ dùng để test, XÓA trong production!
     */
    @GetMapping("/get-reset-token")
    public Map<String, String> getResetToken(@RequestParam String email) {
        Map<String, String> result = new HashMap<>();

        TtaQuanTriVien user = ttaQuanTriVienRepository.findByTtaEmailIgnoreCase(email)
                .orElse(null);

        if (user == null) {
            result.put("error", "User not found");
            return result;
        }

        if (user.getTtaResetToken() == null) {
            result.put("error", "No reset token found");
            return result;
        }

        String resetLink = "http://localhost:8080/user/reset-password?token=" + user.getTtaResetToken();

        result.put("email", user.getTtaEmail());
        result.put("token", user.getTtaResetToken());
        result.put("expiry", user.getTtaResetTokenExpiry().toString());
        result.put("resetLink", resetLink);

        return result;
    }
}
