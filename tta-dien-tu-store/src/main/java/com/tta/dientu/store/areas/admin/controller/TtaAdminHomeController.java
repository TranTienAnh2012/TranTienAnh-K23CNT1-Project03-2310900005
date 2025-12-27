package com.tta.dientu.store.areas.admin.controller;

import com.tta.dientu.store.entity.TtaQuanTriVien;
import com.tta.dientu.store.repository.TtaDonHangRepository;
import com.tta.dientu.store.repository.TtaSanPhamRepository;
import com.tta.dientu.store.repository.TtaQuanTriVienRepository;
import com.tta.dientu.store.repository.TtaDanhGiaRepository;
import com.tta.dientu.store.repository.TtaDanhMucRepository;
import com.tta.dientu.store.repository.TtaBannerRepository;
import com.tta.dientu.store.repository.TtaVoucherRepository;
import com.tta.dientu.store.repository.TtaUserVoucherRepository;
import com.tta.dientu.store.enums.TtaTrangThaiDonHang;
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
    private final TtaDanhMucRepository ttaDanhMucRepository;
    private final TtaBannerRepository ttaBannerRepository;
    private final TtaVoucherRepository ttaVoucherRepository;
    private final TtaUserVoucherRepository ttaUserVoucherRepository;

    @GetMapping({ "", "/", "/dashboard" })
    public String dashboard(Model model) {
        // Thống kê tổng quan
        long tongSanPham = ttaSanPhamRepository.count();
        long tongDonHang = ttaDonHangRepository.count();
        long tongNguoiDung = ttaQuanTriVienRepository.count();
        long tongDanhGia = ttaDanhGiaRepository.count();

        // Thống kê bổ sung
        long tongDanhMuc = ttaDanhMucRepository.count();
        long tongBanner = ttaBannerRepository.count();
        long tongVoucher = ttaVoucherRepository.count();
        long tongUserVoucher = ttaUserVoucherRepository.count();

        // Đơn hàng chưa xử lý (Đã đặt + Đang xử lý)
        long donHangChuaXuLy = ttaDonHangRepository.countByTtaTrangThaiIn(
                List.of(TtaTrangThaiDonHang.DA_DAT, TtaTrangThaiDonHang.DANG_XU_LY));

        // Doanh thu tháng này
        LocalDateTime now = LocalDateTime.now();
        BigDecimal doanhThuThang = ttaDonHangRepository.getTongDoanhThuThang(now.getYear(), now.getMonthValue());
        if (doanhThuThang == null) {
            doanhThuThang = BigDecimal.ZERO;
        }

        List<TtaQuanTriVien> ttaQuanTriVienMoi = ttaQuanTriVienRepository.findTop5ByOrderByTtaNgayDangKyDesc();

        model.addAttribute("pageTitle", "TTA Admin - Dashboard");
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("ttaTongSanPham", tongSanPham);
        model.addAttribute("ttaTongDonHang", tongDonHang);
        model.addAttribute("ttaTongNguoiDung", tongNguoiDung);
        model.addAttribute("ttaTongDanhGia", tongDanhGia);
        model.addAttribute("ttaTongDanhMuc", tongDanhMuc);
        model.addAttribute("ttaTongBanner", tongBanner);
        model.addAttribute("ttaTongVoucher", tongVoucher);
        model.addAttribute("ttaTongUserVoucher", tongUserVoucher);
        model.addAttribute("ttaDonHangChuaXuLy", donHangChuaXuLy);
        model.addAttribute("ttaDoanhThuThang", doanhThuThang);
        model.addAttribute("ttaQuanTriVienMoi", ttaQuanTriVienMoi);

        return "areas/admin/home/tta-dashboard";
    }
}
