package com.tta.dientu.store.areas.admin.controller;

import com.tta.dientu.store.areas.admin.service.TtaAdminDonHangService;
import com.tta.dientu.store.entity.TtaDonHang;
import com.tta.dientu.store.entity.TtaChiTietDonHang;
import com.tta.dientu.store.enums.TtaTrangThaiDonHang;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/donhang")
@RequiredArgsConstructor
public class TtaAdminDonHangController {

    private final TtaAdminDonHangService ttaAdminDonHangService;

    // Danh sách đơn hàng
    @GetMapping("")
    public String listDonHang(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ttaNgayDatHang") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String keyword,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TtaDonHang> ttaDonHangPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            ttaDonHangPage = ttaAdminDonHangService.searchDonHang(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            ttaDonHangPage = ttaAdminDonHangService.getAllDonHang(pageable);
        }

        // Thống kê
        Object[] thongKe = ttaAdminDonHangService.getThongKeDonHang();

        model.addAttribute("pageTitle", "TTA Admin - Quản lý Đơn hàng");
        model.addAttribute("activePage", "TtaDonHang");
        model.addAttribute("ttaDonHangPage", ttaDonHangPage);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("tongDonHang", thongKe[0]);
        model.addAttribute("donHangDaDat", thongKe[1]);
        model.addAttribute("donHangDangXuLy", thongKe[2]);
        model.addAttribute("donHangDangGiao", thongKe[3]);
        model.addAttribute("donHangDaGiao", thongKe[4]);
        model.addAttribute("donHangDaHuy", thongKe[5]);

        // Thêm danh sách trạng thái cho dropdown
        model.addAttribute("allTrangThai", TtaTrangThaiDonHang.values());

        return "areas/admin/TtaDonHang/tta-list";
    }

    // Chi tiết đơn hàng
    @GetMapping("/chitiet/{id}")
    public String TtaChiTietDonHang(@PathVariable Integer id, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<TtaDonHang> ttaDonHangOpt = ttaAdminDonHangService.getDonHangById(id);
        if (ttaDonHangOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng");
            return "redirect:/admin/TtaDonHang";
        }

        List<TtaChiTietDonHang> ttaChiTietList = ttaAdminDonHangService.getChiTietDonHang(id);

        model.addAttribute("pageTitle", "TTA Admin - Chi tiết Đơn hàng #" + id);
        model.addAttribute("activePage", "TtaDonHang");
        model.addAttribute("ttaDonHang", ttaDonHangOpt.get());
        model.addAttribute("ttaChiTietList", ttaChiTietList);

        return "areas/admin/TtaDonHang/tta-detail";
    }

    // Cập nhật trạng thái đơn hàng
    @PostMapping("/trangthai/{id}")
    public String updateTrangThai(@PathVariable Integer id,
            @RequestParam TtaTrangThaiDonHang ttaTrangThai,
            RedirectAttributes redirectAttributes) {
        try {
            boolean updated = ttaAdminDonHangService.updateTrangThaiDonHang(id, ttaTrangThai);
            if (updated) {
                redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái đơn hàng thành công");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
        return "redirect:/admin/donhang/chitiet/" + id;
    }

    // Xóa đơn hàng
    @PostMapping("/xoa/{id}")
    public String xoaDonHang(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            boolean deleted = ttaAdminDonHangService.deleteDonHang(id);
            if (deleted) {
                redirectAttributes.addFlashAttribute("success", "Xóa đơn hàng thành công");
            } else {
                redirectAttributes.addFlashAttribute("error",
                        "Không thể xóa đơn hàng. Chỉ có thể xóa đơn hàng chưa xử lý");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa đơn hàng: " + e.getMessage());
        }
        return "redirect:/admin/TtaDonHang";
    }

    // Đơn hàng chưa xử lý
    @GetMapping("/chuaxuly")
    public String donHangChuaXuLy(Model model) {
        List<TtaDonHang> ttaDonHangList = ttaAdminDonHangService.getDonHangChuaXuLy();

        model.addAttribute("pageTitle", "TTA Admin - Đơn hàng Chưa xử lý");
        model.addAttribute("activePage", "TtaDonHang");
        model.addAttribute("ttaDonHangList", ttaDonHangList);
        model.addAttribute("isChuaXuLyPage", true);

        return "areas/admin/TtaDonHang/tta-list-chuaxuly";
    }
}