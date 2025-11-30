package com.tta.dientu.store.areas.admin.controller;

import com.tta.dientu.store.entity.TtaQuanTriVien;
import com.tta.dientu.store.repository.TtaDonHangRepository;
import com.tta.dientu.store.repository.TtaSanPhamRepository;
import com.tta.dientu.store.repository.TtaQuanTriVienRepository;
import com.tta.dientu.store.repository.TtaDanhGiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class TtaAdminHomeController {

    private final TtaDonHangRepository ttaDonHangRepository;
    private final TtaSanPhamRepository ttaSanPhamRepository;
    private final TtaQuanTriVienRepository ttaQuanTriVienRepository;
    private final TtaDanhGiaRepository ttaDanhGiaRepository;

    @GetMapping({ "", "/", "/dashboard" })
    public String dashboard(Model model) {
        // Thống kê tổng quan
        long tongSanPham = ttaSanPhamRepository.count();
        long tongDonHang = ttaDonHangRepository.count();
        long tongNguoiDung = ttaQuanTriVienRepository.count();
        long tongDanhGia = ttaDanhGiaRepository.count();

        // Đơn hàng chưa xử lý
        long donHangChuaXuLy = ttaDonHangRepository.countByTtaTrangThaiFalse();

        // Doanh thu tháng này
        LocalDateTime now = LocalDateTime.now();
        BigDecimal doanhThuThang = ttaDonHangRepository.getTongDoanhThuThang(now.getYear(), now.getMonthValue());
        if (doanhThuThang == null) {
            doanhThuThang = BigDecimal.ZERO;
        }

        List<TtaQuanTriVien> ttaQuanTriVienMoi = ttaQuanTriVienRepository.findTop5ByOrderByTtaNgayDangKyDesc();

        model.addAttribute("pageTitle", "TTA Admin - Dashboard");
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("tongSanPham", tongSanPham);
        model.addAttribute("tongDonHang", tongDonHang);
        model.addAttribute("tongNguoiDung", tongNguoiDung);
        model.addAttribute("tongDanhGia", tongDanhGia);
        model.addAttribute("donHangChuaXuLy", donHangChuaXuLy);
        model.addAttribute("doanhThuThang", doanhThuThang);
        model.addAttribute("ttaQuanTriVienMoi", ttaQuanTriVienMoi);

        return "areas/admin/home/tta-dashboard";
    }
}
