package com.tta.dientu.store.areas.user.service;

import com.tta.dientu.store.entity.TtaQuanTriVien;
import com.tta.dientu.store.repository.TtaQuanTriVienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TtaCustomUserDetailsService implements UserDetailsService {

    private final TtaQuanTriVienRepository ttaQuanTriVienRepository;

    /**
     * Load user từ bảng QuanTriVien trong database theo email
     * Được Spring Security gọi tự động khi đăng nhập
     * 
     * Flow khi đăng nhập:
     * 1. User nhập email và mật khẩu
     * 2. Spring Security gọi method này với email
     * 3. Tìm kiếm trong bảng QuanTriVien theo email
     * 4. Nếu tìm thấy -> trả về UserDetails để Spring Security kiểm tra mật khẩu
     * 5. Nếu không tìm thấy -> throw UsernameNotFoundException -> đăng nhập thất
     * bại
     * 
     * Lưu ý: Tài khoản phải được lưu vào bảng QuanTriVien trước (qua đăng ký hoặc
     * insert trực tiếp)
     * 
     * @param email Email của người dùng (từ form đăng nhập)
     * @return UserDetails chứa thông tin user và role từ bảng QuanTriVien
     * @throws UsernameNotFoundException Nếu không tìm thấy tài khoản trong bảng
     *                                   QuanTriVien
     */
    @Override
    public UserDetails loadUserByUsername(String ttaEmail) throws UsernameNotFoundException {
        // ========== TÌM KIẾM TRONG BẢNG QUANTRIVIEN ==========
        // Tìm kiếm tài khoản trong bảng QuanTriVien theo email
        // Nếu không tìm thấy -> throw exception -> đăng nhập thất bại
        System.out.println("=== DEBUG: Đang tìm kiếm user với email: " + ttaEmail + " ===");

        // Debug: List tất cả users trong database
        System.out.println("=== DEBUG: Tất cả users trong database ===");
        ttaQuanTriVienRepository.findAll().forEach(user -> {
            System.out.println("  - ID: " + user.getTtaMaNguoiDung() +
                    ", Email: '" + user.getTtaEmail() + "'" +
                    ", Họ tên: " + user.getTtaHoTen() +
                    ", Vai trò: " + user.getTtaVaiTro());
        });

        // Thử tìm với case-sensitive trước
        Optional<TtaQuanTriVien> userOpt = ttaQuanTriVienRepository.findByTtaEmail(ttaEmail);

        // Nếu không tìm thấy, thử case-insensitive
        if (userOpt.isEmpty()) {
            System.out.println("=== DEBUG: Không tìm thấy với case-sensitive, thử case-insensitive ===");
            userOpt = ttaQuanTriVienRepository.findByTtaEmailIgnoreCase(ttaEmail);
        }

        TtaQuanTriVien ttaQuanTriVien = userOpt.orElseThrow(() -> {
            System.out.println("=== DEBUG: KHÔNG TÌM THẤY user với email: " + ttaEmail + " ===");
            return new UsernameNotFoundException(
                    "Không tìm thấy tài khoản với email: " + ttaEmail + " trong bảng QuanTriVien");
        });

        System.out.println("=== DEBUG: Tìm thấy user ===");
        System.out.println("  - Email: " + ttaQuanTriVien.getTtaEmail());
        System.out.println("  - Họ tên: " + ttaQuanTriVien.getTtaHoTen());
        System.out.println("  - Vai trò: " + ttaQuanTriVien.getTtaVaiTro());
        System.out.println("  - Mật khẩu: " + (ttaQuanTriVien.getTtaMatKhau() != null ? "***" : "NULL"));

        // ========== TẠO USERDETAILS TỪ DỮ LIỆU QUANTRIVIEN ==========
        // Tạo CustomUserDetails với thông tin user từ bảng QuanTriVien
        // Role được xác định trong CustomUserDetails.getAuthorities():
        // - vaiTro == 1 -> ROLE_ADMIN
        // - vaiTro == 0 hoặc null -> ROLE_USER
        TtaCustomUserDetails userDetails = new TtaCustomUserDetails(ttaQuanTriVien);
        System.out.println("=== DEBUG: Đã tạo CustomUserDetails ===");
        return userDetails;
    }
}
