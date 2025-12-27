package com.tta.dientu.store.areas.user.controller;

import com.tta.dientu.store.areas.user.service.TtaPasswordResetService;
import com.tta.dientu.store.entity.TtaQuanTriVien;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class TtaUserAccountController {

    private final TtaPasswordResetService ttaPasswordResetService;

    /**
     * Hiển thị form quên mật khẩu
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("pageTitle", "Quên mật khẩu - TTA Store");
        return "areas/user/account/tta-forgot-password";
    }

    /**
     * Xử lý yêu cầu quên mật khẩu
     */
    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam("email") String email,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        try {
            // Tạo base URL
            String baseUrl = request.getScheme() + "://" +
                    request.getServerName() + ":" +
                    request.getServerPort() +
                    request.getContextPath();

            boolean success = ttaPasswordResetService.createResetToken(email, baseUrl);

            if (success) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Email đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra hộp thư của bạn.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Không tìm thấy tài khoản với email này.");
            }
        } catch (Exception e) {
            // DEBUG MODE: Nếu gửi email thất bại, hiển thị link reset trực tiếp
            try {
                String baseUrl = request.getScheme() + "://" +
                        request.getServerName() + ":" +
                        request.getServerPort() +
                        request.getContextPath();

                String resetLink = ttaPasswordResetService.getResetLinkForDebug(email, baseUrl);

                if (resetLink != null) {
                    redirectAttributes.addFlashAttribute("warningMessage",
                            "⚠️ SMTP không khả dụng. Link reset password (chỉ dùng để test):");
                    redirectAttributes.addFlashAttribute("resetLink", resetLink);
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "Không tìm thấy tài khoản với email này.");
                }
            } catch (Exception ex) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Đã xảy ra lỗi. Vui lòng thử lại sau.");
            }
            e.printStackTrace();
        }

        return "redirect:/user/forgot-password";
    }

    /**
     * Hiển thị form đặt lại mật khẩu
     */
    @GetMapping("/reset-password")
    public String showResetPasswordForm(
            @RequestParam("token") String token,
            Model model,
            RedirectAttributes redirectAttributes) {

        Optional<TtaQuanTriVien> userOpt = ttaPasswordResetService.validateToken(token);

        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Link đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.");
            return "redirect:/user/forgot-password";
        }

        model.addAttribute("pageTitle", "Đặt lại mật khẩu - TTA Store");
        model.addAttribute("token", token);
        return "areas/user/account/tta-reset-password";
    }

    /**
     * Xử lý đặt lại mật khẩu
     */
    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {

        // Kiểm tra mật khẩu khớp
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Mật khẩu xác nhận không khớp.");
            redirectAttributes.addAttribute("token", token);
            return "redirect:/user/reset-password";
        }

        // Kiểm tra độ dài mật khẩu
        if (password.length() < 6) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Mật khẩu phải có ít nhất 6 ký tự.");
            redirectAttributes.addAttribute("token", token);
            return "redirect:/user/reset-password";
        }

        boolean success = ttaPasswordResetService.resetPassword(token, password);

        if (success) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Mật khẩu đã được đặt lại thành công. Vui lòng đăng nhập.");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Link đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.");
            return "redirect:/user/forgot-password";
        }
    }
}
