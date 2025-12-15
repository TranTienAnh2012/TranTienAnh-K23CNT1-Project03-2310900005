package com.tta.dientu.store.areas.user.controller;

import com.tta.dientu.store.entity.TtaQuanTriVien;
import com.tta.dientu.store.repository.TtaQuanTriVienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class TtaAccountController {

    private final TtaQuanTriVienRepository ttaQuanTriVienRepository;

    /**
     * Redirect sau khi đăng nhập thành công dựa trên vai trò
     * Logic:
     * 1. Spring Security đã kiểm tra tài khoản tồn tại và mật khẩu đúng
     * 2. TtaCustomUserDetailsService đã load user và set role (ROLE_ADMIN hoặc
     * ROLE_USER)
     * 3. Method này kiểm tra role và redirect đến giao diện tương ứng
     */
    @GetMapping("/redirect-by-role")
    public String redirectByRole(Authentication authentication) {
        // Kiểm tra xem user đã đăng nhập chưa
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/account/login";
        }

        // Redirect all users (including admin) to user product page
        // Admin can access admin panel via the "Admin" button in navigation
        return "redirect:/user/san-pham";
    }

    // Hiển thị form đăng nhập
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("pageTitle", "TTA Store - Đăng nhập");
        model.addAttribute("ttaUser", new TtaQuanTriVien()); // Added for Register form in the same view

        // Debug: Hiển thị danh sách users có trong database (chỉ để debug)
        var allUsers = ttaQuanTriVienRepository.findAll();
        if (!allUsers.isEmpty()) {
            System.out.println("=== DEBUG: Danh sách users có thể đăng nhập ===");
            allUsers.forEach(user -> {
                System.out.println("  - Email: " + user.getTtaEmail() +
                        ", Họ tên: " + user.getTtaHoTen() +
                        ", Vai trò: " + (user.getTtaVaiTro() == 1 ? "ADMIN" : "USER"));
            });
        }

        return "account/tta-login";
    }

    /**
     * Hiển thị form đăng ký
     * Form sẽ bind dữ liệu vào đối tượng TtaQuanTriVien
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("pageTitle", "TTA Store - Đăng ký");
        // Tạo đối tượng TtaQuanTriVien mới để bind dữ liệu từ form
        model.addAttribute("ttaUser", new TtaQuanTriVien());
        return "account/tta-register";
    }

    /**
     * Xử lý đăng ký - Lưu dữ liệu vào bảng TtaQuanTriVien
     * Flow:
     * 1. Nhận dữ liệu từ form (bind vào đối tượng TtaQuanTriVien)
     * 2. Validation dữ liệu
     * 3. Kiểm tra email đã tồn tại trong TtaQuanTriVien chưa
     * 4. Set thông tin mặc định (vaiTro = 0, ngayDangKy = now)
     * 5. Lưu vào database bảng TtaQuanTriVien qua TtaQuanTriVienRepository.save()
     * 
     * Sau khi đăng ký thành công, user có thể đăng nhập và hệ thống sẽ kiểm tra
     * tài khoản từ bảng TtaQuanTriVien qua
     * TtaCustomUserDetailsService.loadUserByUsername()
     */
    @PostMapping("/register")
    public String processRegister(@ModelAttribute TtaQuanTriVien ttaUser,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {
        try {
            // ========== VALIDATION DỮ LIỆU ==========
            // Kiểm tra họ tên
            if (ttaUser.getTtaHoTen() == null || ttaUser.getTtaHoTen().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Họ tên không được để trống");
                return "redirect:/account/register";
            }

            // Kiểm tra email
            if (ttaUser.getTtaEmail() == null || ttaUser.getTtaEmail().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Email không được để trống");
                return "redirect:/account/register";
            }

            // Kiểm tra mật khẩu
            if (ttaUser.getTtaMatKhau() == null || ttaUser.getTtaMatKhau().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu không được để trống");
                return "redirect:/account/register";
            }

            // Kiểm tra mật khẩu xác nhận
            if (!ttaUser.getTtaMatKhau().equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp");
                return "redirect:/account/register";
            }

            // ========== KIỂM TRA EMAIL ĐÃ TỒN TẠI TRONG TtaQuanTriVien ==========
            // Kiểm tra email đã tồn tại trong bảng TtaQuanTriVien chưa
            if (ttaQuanTriVienRepository.existsByTtaEmail(ttaUser.getTtaEmail())) {
                redirectAttributes.addFlashAttribute("error", "Email đã được sử dụng");
                return "redirect:/account/register";
            }

            // ========== SET THÔNG TIN MẶC ĐỊNH ==========
            ttaUser.setTtaVaiTro(0); // Mặc định là USER (0), ADMIN = 1
            ttaUser.setTtaNgayDangKy(LocalDateTime.now());
            // Không mã hóa password - lưu plain text vào database

            // ========== LƯU VÀO BẢNG TtaQuanTriVien ==========
            // Lưu đối tượng TtaQuanTriVien vào database
            // Sau khi lưu, user có thể đăng nhập và hệ thống sẽ kiểm tra từ bảng này
            TtaQuanTriVien savedUser = ttaQuanTriVienRepository.save(ttaUser);

            // Kiểm tra đã lưu thành công
            if (savedUser.getTtaMaNguoiDung() != null) {
                redirectAttributes.addFlashAttribute("success",
                        "Đăng ký thành công! Tài khoản đã được lưu vào hệ thống. Vui lòng đăng nhập.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Lỗi khi lưu tài khoản!");
                return "redirect:/account/register";
            }

            return "redirect:/account/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi đăng ký: " + e.getMessage());
            return "redirect:/account/register";
        }
    }

    // Hiển thị trang profile
    @GetMapping("/profile")
    public String showProfile(Model model) {
        // Lấy user hiện tại từ Security Context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();

        Optional<TtaQuanTriVien> ttaUserOpt = ttaQuanTriVienRepository.findByTtaEmail(currentEmail);

        if (ttaUserOpt.isPresent()) {
            model.addAttribute("ttaUser", ttaUserOpt.get());
        } else {
            model.addAttribute("ttaUser", new TtaQuanTriVien());
        }

        model.addAttribute("pageTitle", "TTA Store - Hồ sơ");
        return "account/tta-profile";
    }

    // Cập nhật profile
    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute TtaQuanTriVien ttaUser,
            @RequestParam(required = false) String newPassword,
            @RequestParam(required = false) String confirmPassword,
            RedirectAttributes redirectAttributes) {
        try {
            // Lấy user hiện tại từ Security Context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentEmail = authentication.getName();

            Optional<TtaQuanTriVien> existingUserOpt = ttaQuanTriVienRepository.findByTtaEmail(currentEmail);

            if (existingUserOpt.isPresent()) {
                TtaQuanTriVien existingUser = existingUserOpt.get();

                // Cập nhật thông tin cơ bản
                existingUser.setTtaHoTen(ttaUser.getTtaHoTen());
                existingUser.setTtaEmail(ttaUser.getTtaEmail());

                // Cập nhật mật khẩu nếu có
                if (newPassword != null && !newPassword.trim().isEmpty()) {
                    if (!newPassword.equals(confirmPassword)) {
                        redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp!");
                        return "redirect:/account/profile";
                    }
                    // Không mã hóa password - lưu plain text
                    existingUser.setTtaMatKhau(newPassword);
                }

                ttaQuanTriVienRepository.save(existingUser);
                redirectAttributes.addFlashAttribute("success", "Cập nhật hồ sơ thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng!");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật: " + e.getMessage());
        }

        return "redirect:/account/profile";
    }

    // Đăng xuất đã được Spring Security xử lý
    // Quên mật khẩu
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("pageTitle", "TTA Store - Quên mật khẩu");
        return "account/tta-forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String ttaEmail,
            RedirectAttributes redirectAttributes) {
        Optional<TtaQuanTriVien> ttaUserOpt = ttaQuanTriVienRepository.findByTtaEmail(ttaEmail);

        if (ttaUserOpt.isPresent()) {
            // Tạm thời chỉ thông báo (sau này sẽ gửi email)
            redirectAttributes.addFlashAttribute("success",
                    "Hướng dẫn reset mật khẩu đã được gửi đến email: " + ttaEmail);
        } else {
            redirectAttributes.addFlashAttribute("error", "Email không tồn tại trong hệ thống!");
        }

        return "redirect:/account/forgot-password";
    }

    // Access denied page
    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("pageTitle", "Truy cập bị từ chối");
        return "error/tta-access-denied";
    }
}