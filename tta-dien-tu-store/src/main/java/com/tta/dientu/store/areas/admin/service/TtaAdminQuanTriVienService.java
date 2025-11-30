package com.tta.dientu.store.areas.admin.service;

import com.tta.dientu.store.entity.TtaQuanTriVien;
import com.tta.dientu.store.repository.TtaQuanTriVienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TtaAdminQuanTriVienService {

    private final TtaQuanTriVienRepository ttaQuanTriVienRepository;

    public Page<TtaQuanTriVien> getQuanTriVienPage(Pageable pageable, String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            String value = keyword.trim();
            return ttaQuanTriVienRepository
                    .findByTtaHoTenContainingIgnoreCaseOrTtaEmailContainingIgnoreCase(value, value, pageable);
        }
        return ttaQuanTriVienRepository.findAll(pageable);
    }

    public Optional<TtaQuanTriVien> getQuanTriVienById(Integer id) {
        return ttaQuanTriVienRepository.findById(id);
    }

    public TtaQuanTriVien saveQuanTriVien(TtaQuanTriVien ttaQuanTriVien) {
        validateQuanTriVien(ttaQuanTriVien);
        return ttaQuanTriVienRepository.save(ttaQuanTriVien);
    }

    public boolean deleteQuanTriVien(Integer id) {
        if (ttaQuanTriVienRepository.existsById(id)) {
            ttaQuanTriVienRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean isEmailExists(String ttaEmail, Integer excludeId) {
        if (ttaEmail == null || ttaEmail.trim().isEmpty()) {
            return false;
        }

        if (excludeId == null) {
            return ttaQuanTriVienRepository.existsByTtaEmail(ttaEmail);
        }
        return ttaQuanTriVienRepository.existsByTtaEmailAndTtaMaNguoiDungNot(ttaEmail, excludeId);
    }

    public List<TtaQuanTriVien> getLatestQuanTriVien(int limit) {
        List<TtaQuanTriVien> latest = ttaQuanTriVienRepository.findTop5ByOrderByTtaNgayDangKyDesc();
        if (limit > 0 && latest.size() > limit) {
            return latest.subList(0, limit);
        }
        return latest;
    }

    private void validateQuanTriVien(TtaQuanTriVien ttaQuanTriVien) {
        if (ttaQuanTriVien.getTtaHoTen() == null || ttaQuanTriVien.getTtaHoTen().trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống");
        }
        if (ttaQuanTriVien.getTtaEmail() == null || ttaQuanTriVien.getTtaEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        if (ttaQuanTriVien.getTtaMatKhau() == null || ttaQuanTriVien.getTtaMatKhau().trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        if (ttaQuanTriVien.getTtaVaiTro() == null) {
            ttaQuanTriVien.setTtaVaiTro(0);
        }
    }
}
