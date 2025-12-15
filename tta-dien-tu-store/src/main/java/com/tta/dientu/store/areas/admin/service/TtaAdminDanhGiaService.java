package com.tta.dientu.store.areas.admin.service;

import com.tta.dientu.store.entity.TtaDanhGia;
import com.tta.dientu.store.entity.TtaSanPham;
import com.tta.dientu.store.entity.TtaQuanTriVien;
import com.tta.dientu.store.repository.TtaDanhGiaRepository;
import com.tta.dientu.store.repository.TtaSanPhamRepository;
import com.tta.dientu.store.repository.TtaQuanTriVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TtaAdminDanhGiaService {

    @Autowired
    private TtaDanhGiaRepository ttaDanhGiaRepository;

    @Autowired
    private TtaSanPhamRepository ttaSanPhamRepository;

    @Autowired
    private TtaQuanTriVienRepository ttaQuanTriVienRepository;

    // Lấy tất cả đánh giá với phân trang
    public Page<TtaDanhGia> getAllReviews(Pageable pageable) {
        return ttaDanhGiaRepository.findAll(pageable);
    }

    // Lấy đánh giá theo ID
    public Optional<TtaDanhGia> getReviewById(Integer id) {
        return ttaDanhGiaRepository.findById(id);
    }

    // Tạo đánh giá mới
    public TtaDanhGia createReview(Integer maSanPham, Integer maNguoiDung, Integer soSao, String binhLuan) {
        Optional<TtaSanPham> sanPhamOpt = ttaSanPhamRepository.findById(maSanPham);
        Optional<TtaQuanTriVien> userOpt = ttaQuanTriVienRepository.findById(maNguoiDung);

        if (sanPhamOpt.isEmpty() || userOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy sản phẩm hoặc người dùng!");
        }

        TtaDanhGia danhGia = new TtaDanhGia();
        danhGia.setTtaSanPham(sanPhamOpt.get());
        danhGia.setTtaNguoiDung(userOpt.get());
        danhGia.setTtaSoSao(soSao);
        danhGia.setTtaBinhLuan(binhLuan);
        danhGia.setTtaNgayDanhGia(LocalDateTime.now());

        return ttaDanhGiaRepository.save(danhGia);
    }

    // Cập nhật đánh giá
    public TtaDanhGia updateReview(Integer id, Integer soSao, String binhLuan) {
        Optional<TtaDanhGia> danhGiaOpt = ttaDanhGiaRepository.findById(id);

        if (danhGiaOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy đánh giá!");
        }

        TtaDanhGia danhGia = danhGiaOpt.get();
        danhGia.setTtaSoSao(soSao);
        danhGia.setTtaBinhLuan(binhLuan);

        return ttaDanhGiaRepository.save(danhGia);
    }

    // Xóa đánh giá
    public void deleteReview(Integer id) {
        ttaDanhGiaRepository.deleteById(id);
    }

    // Lấy tất cả sản phẩm (cho dropdown)
    public List<TtaSanPham> getAllProducts() {
        return ttaSanPhamRepository.findAll();
    }

    // Lấy tất cả users (cho dropdown)
    public List<TtaQuanTriVien> getAllUsers() {
        return ttaQuanTriVienRepository.findAll();
    }
}
