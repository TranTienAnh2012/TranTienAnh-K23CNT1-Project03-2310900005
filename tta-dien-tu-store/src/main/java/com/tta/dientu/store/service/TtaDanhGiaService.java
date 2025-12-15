package com.tta.dientu.store.service;

import com.tta.dientu.store.entity.TtaDanhGia;
import com.tta.dientu.store.entity.TtaSanPham;
import com.tta.dientu.store.entity.TtaQuanTriVien;
import com.tta.dientu.store.repository.TtaDanhGiaRepository;
import com.tta.dientu.store.repository.TtaSanPhamRepository;
import com.tta.dientu.store.repository.TtaQuanTriVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TtaDanhGiaService {

    @Autowired
    private TtaDanhGiaRepository ttaDanhGiaRepository;

    @Autowired
    private TtaSanPhamRepository ttaSanPhamRepository;

    @Autowired
    private TtaQuanTriVienRepository ttaQuanTriVienRepository;

    @Autowired
    private com.tta.dientu.store.repository.TtaDonHangRepository ttaDonHangRepository;

    // Lấy tất cả đánh giá của sản phẩm
    public List<TtaDanhGia> getReviewsByProduct(Integer maSanPham) {
        return ttaDanhGiaRepository.findByTtaSanPham_TtaMaSanPham(maSanPham);
    }

    // Lấy rating trung bình
    public Double getAverageRating(Integer maSanPham) {
        Double avg = ttaDanhGiaRepository.getDiemTrungBinhBySanPham(maSanPham);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    // Đếm số lượng đánh giá
    public Long getReviewCount(Integer maSanPham) {
        return ttaDanhGiaRepository.countByTtaSanPham_TtaMaSanPham(maSanPham);
    }

    // Kiểm tra user đã đánh giá chưa
    public boolean hasUserReviewed(Integer maSanPham, Integer maNguoiDung) {
        return ttaDanhGiaRepository.findByTtaSanPham_TtaMaSanPhamAndTtaNguoiDung_TtaMaNguoiDung(
                maSanPham, maNguoiDung).isPresent();
    }

    // Kiểm tra user đã mua sản phẩm chưa
    public boolean hasUserPurchasedProduct(Integer maSanPham, Integer maNguoiDung) {
        // Sử dụng query trực tiếp thay vì load entities để tránh circular reference
        return ttaDonHangRepository.findByTtaNguoiDung_TtaMaNguoiDung(maNguoiDung).stream()
                .filter(donHang -> donHang.getTtaTrangThai() == com.tta.dientu.store.enums.TtaTrangThaiDonHang.DA_GIAO
                        ||
                        donHang.getTtaTrangThai() == com.tta.dientu.store.enums.TtaTrangThaiDonHang.DANG_GIAO)
                .flatMap(donHang -> {
                    try {
                        return donHang.getTtaChiTietDonHangs().stream();
                    } catch (Exception e) {
                        return java.util.stream.Stream.empty();
                    }
                })
                .anyMatch(chiTiet -> {
                    try {
                        return chiTiet.getTtaSanPham() != null &&
                                chiTiet.getTtaSanPham().getTtaMaSanPham().equals(maSanPham);
                    } catch (Exception e) {
                        return false;
                    }
                });
    }

    // Gửi đánh giá mới
    public TtaDanhGia submitReview(Integer maSanPham, Integer maNguoiDung, Integer soSao, String binhLuan) {
        // Kiểm tra đã mua sản phẩm chưa
        if (!hasUserPurchasedProduct(maSanPham, maNguoiDung)) {
            throw new RuntimeException("Bạn cần mua sản phẩm này trước khi đánh giá!");
        }

        // Kiểm tra đã đánh giá chưa
        if (hasUserReviewed(maSanPham, maNguoiDung)) {
            throw new RuntimeException("Bạn đã đánh giá sản phẩm này rồi!");
        }

        // Lấy sản phẩm và user
        Optional<TtaSanPham> sanPhamOpt = ttaSanPhamRepository.findById(maSanPham);
        Optional<TtaQuanTriVien> userOpt = ttaQuanTriVienRepository.findById(maNguoiDung);

        if (sanPhamOpt.isEmpty() || userOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy sản phẩm hoặc người dùng!");
        }

        // Tạo đánh giá mới
        TtaDanhGia danhGia = new TtaDanhGia();
        danhGia.setTtaSanPham(sanPhamOpt.get());
        danhGia.setTtaNguoiDung(userOpt.get());
        danhGia.setTtaSoSao(soSao);
        danhGia.setTtaBinhLuan(binhLuan);
        danhGia.setTtaNgayDanhGia(LocalDateTime.now());

        return ttaDanhGiaRepository.save(danhGia);
    }
}
