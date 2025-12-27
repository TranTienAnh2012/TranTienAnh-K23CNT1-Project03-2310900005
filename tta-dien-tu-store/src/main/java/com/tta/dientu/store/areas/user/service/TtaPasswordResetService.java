package com.tta.dientu.store.areas.user.service;

import com.tta.dientu.store.entity.TtaQuanTriVien;
import com.tta.dientu.store.repository.TtaQuanTriVienRepository;
import com.tta.dientu.store.service.TtaMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TtaPasswordResetService {

    private final TtaQuanTriVienRepository ttaQuanTriVienRepository;
    private final TtaMailService ttaMailService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Tạo token reset password và gửi email
     * 
     * @param email   Email của người dùng
     * @param baseUrl URL gốc của ứng dụng
     * @return true nếu thành công, false nếu không tìm thấy email
     */
    public boolean createResetToken(String email, String baseUrl) {
        Optional<TtaQuanTriVien> userOpt = ttaQuanTriVienRepository.findByTtaEmailIgnoreCase(email);

        if (userOpt.isEmpty()) {
            return false;
        }

        TtaQuanTriVien user = userOpt.get();

        // Tạo token ngẫu nhiên
        String token = UUID.randomUUID().toString();

        // Lưu token và thời gian hết hạn (30 phút)
        user.setTtaResetToken(token);
        user.setTtaResetTokenExpiry(LocalDateTime.now().plusMinutes(30));
        ttaQuanTriVienRepository.save(user);

        // Tạo link reset password
        String resetLink = baseUrl + "/user/reset-password?token=" + token;

        // Gửi email
        ttaMailService.sendResetPasswordEmail(email, resetLink);

        return true;
    }

    /**
     * Kiểm tra token có hợp lệ không
     * 
     * @param token Token cần kiểm tra
     * @return Optional chứa user nếu token hợp lệ
     */
    public Optional<TtaQuanTriVien> validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return Optional.empty();
        }

        Optional<TtaQuanTriVien> userOpt = ttaQuanTriVienRepository.findByTtaResetToken(token);

        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        TtaQuanTriVien user = userOpt.get();

        // Kiểm tra token đã hết hạn chưa
        if (user.getTtaResetTokenExpiry() == null ||
                user.getTtaResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return Optional.empty();
        }

        return userOpt;
    }

    /**
     * Đặt lại mật khẩu
     * 
     * @param token       Token reset
     * @param newPassword Mật khẩu mới
     * @return true nếu thành công
     */
    public boolean resetPassword(String token, String newPassword) {
        Optional<TtaQuanTriVien> userOpt = validateToken(token);

        if (userOpt.isEmpty()) {
            return false;
        }

        TtaQuanTriVien user = userOpt.get();

        // Mã hóa và lưu mật khẩu mới
        user.setTtaMatKhau(passwordEncoder.encode(newPassword));

        // Xóa token
        user.setTtaResetToken(null);
        user.setTtaResetTokenExpiry(null);

        ttaQuanTriVienRepository.save(user);

        return true;
    }

    /**
     * Tạo reset link cho debug (không gửi email)
     * CHỈ DÙNG KHI SMTP KHÔNG KHẢ DỤNG
     * 
     * @param email   Email của người dùng
     * @param baseUrl URL gốc của ứng dụng
     * @return Reset link hoặc null nếu không tìm thấy email
     */
    public String getResetLinkForDebug(String email, String baseUrl) {
        Optional<TtaQuanTriVien> userOpt = ttaQuanTriVienRepository.findByTtaEmailIgnoreCase(email);

        if (userOpt.isEmpty()) {
            return null;
        }

        TtaQuanTriVien user = userOpt.get();

        // Tạo token ngẫu nhiên
        String token = UUID.randomUUID().toString();

        // Lưu token và thời gian hết hạn (30 phút)
        user.setTtaResetToken(token);
        user.setTtaResetTokenExpiry(LocalDateTime.now().plusMinutes(30));
        ttaQuanTriVienRepository.save(user);

        // Tạo và trả về link reset password
        return baseUrl + "/user/reset-password?token=" + token;
    }
}
