package com.tta.dientu.store.repository;

import com.tta.dientu.store.entity.TtaQuanTriVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TtaQuanTriVienRepository extends JpaRepository<TtaQuanTriVien, Integer> {

    // Tìm user bằng email (case-insensitive)
    Optional<TtaQuanTriVien> findByTtaEmailIgnoreCase(String ttaEmail);

    // Tìm user bằng email (case-sensitive - mặc định)
    Optional<TtaQuanTriVien> findByTtaEmail(String ttaEmail);

    // Kiểm tra email đã tồn tại chưa
    boolean existsByTtaEmail(String ttaEmail);

    // Kiểm tra email trùng khi cập nhật (loại trừ chính bản ghi đó)
    boolean existsByTtaEmailAndTtaMaNguoiDungNot(String ttaEmail, Integer ttaMaNguoiDung);

    // Lấy top quản trị viên mới nhất
    List<TtaQuanTriVien> findTop5ByOrderByTtaNgayDangKyDesc();

    // Tìm user bằng reset token
    Optional<TtaQuanTriVien> findByTtaResetToken(String ttaResetToken);

    // Tìm kiếm theo họ tên hoặc email
    Page<TtaQuanTriVien> findByTtaHoTenContainingIgnoreCaseOrTtaEmailContainingIgnoreCase(
            String ttaHoTenKeyword,
            String ttaEmailKeyword,
            Pageable pageable);
}
