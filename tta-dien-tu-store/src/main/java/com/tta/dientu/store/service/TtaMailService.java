package com.tta.dientu.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TtaMailService {

    private final JavaMailSender mailSender;

    /**
     * Gửi email reset password
     * 
     * @param to        Email người nhận
     * @param resetLink Link reset password
     */
    public void sendResetPasswordEmail(String to, String resetLink) {
        try {
            System.out.println("🔄 Đang chuẩn bị gửi email đến: " + to);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Đặt lại mật khẩu - TTA Store");
            message.setText("Xin chào,\n\n" +
                    "Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản của mình.\n\n" +
                    "Vui lòng nhấp vào link sau để đặt lại mật khẩu:\n" +
                    resetLink + "\n\n" +
                    "Link này sẽ hết hạn sau 30 phút.\n\n" +
                    "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\n" +
                    "Trân trọng,\n" +
                    "TTA Store");

            System.out.println("📧 Đang gửi email qua SMTP...");
            mailSender.send(message);
            System.out.println("✅ Email đã được gửi thành công đến: " + to);
        } catch (Exception e) {
            System.err.println("❌ LỖI khi gửi email: " + e.getMessage());
            System.err.println("❌ Loại lỗi: " + e.getClass().getName());

            // Print root cause
            Throwable cause = e.getCause();
            if (cause != null) {
                System.err.println("❌ Nguyên nhân gốc: " + cause.getMessage());
                System.err.println("❌ Loại nguyên nhân: " + cause.getClass().getName());
            }

            e.printStackTrace();
            throw new RuntimeException("Không thể gửi email. Vui lòng thử lại sau.");
        }
    }
}
