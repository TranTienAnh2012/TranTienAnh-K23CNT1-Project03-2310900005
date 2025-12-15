package com.tta.dientu.store.areas.admin.controller;

import com.tta.dientu.store.areas.admin.service.TtaAdminDanhGiaService;
import com.tta.dientu.store.entity.TtaDanhGia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/danhgia")
public class TtaAdminDanhGiaController {

    @Autowired
    private TtaAdminDanhGiaService ttaAdminDanhGiaService;

    // Danh sách đánh giá
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("ttaNgayDanhGia").descending());
        Page<TtaDanhGia> danhGiaPage = ttaAdminDanhGiaService.getAllReviews(pageable);

        model.addAttribute("pageTitle", "TTA Admin - Quản lý Đánh giá");
        model.addAttribute("activePage", "TtaDanhGia");
        model.addAttribute("danhGiaPage", danhGiaPage);

        return "areas/admin/TtaDanhGia/tta-list";
    }

    // Hiển thị form tạo mới
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("pageTitle", "TTA Admin - Thêm Đánh giá");
        model.addAttribute("activePage", "TtaDanhGia");
        model.addAttribute("products", ttaAdminDanhGiaService.getAllProducts());
        model.addAttribute("users", ttaAdminDanhGiaService.getAllUsers());

        return "areas/admin/TtaDanhGia/tta-create";
    }

    // Xử lý tạo mới
    @PostMapping("/create")
    public String create(
            @RequestParam Integer maSanPham,
            @RequestParam Integer maNguoiDung,
            @RequestParam Integer soSao,
            @RequestParam String binhLuan,
            RedirectAttributes redirectAttributes) {

        try {
            ttaAdminDanhGiaService.createReview(maSanPham, maNguoiDung, soSao, binhLuan);
            redirectAttributes.addFlashAttribute("success", "Thêm đánh giá thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/danhgia";
    }

    // Hiển thị form chỉnh sửa
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<TtaDanhGia> danhGiaOpt = ttaAdminDanhGiaService.getReviewById(id);

        if (danhGiaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đánh giá!");
            return "redirect:/admin/danhgia";
        }

        model.addAttribute("pageTitle", "TTA Admin - Sửa Đánh giá");
        model.addAttribute("activePage", "TtaDanhGia");
        model.addAttribute("danhGia", danhGiaOpt.get());

        return "areas/admin/TtaDanhGia/tta-edit";
    }

    // Xử lý cập nhật
    @PostMapping("/edit/{id}")
    public String update(
            @PathVariable Integer id,
            @RequestParam Integer soSao,
            @RequestParam String binhLuan,
            RedirectAttributes redirectAttributes) {

        try {
            ttaAdminDanhGiaService.updateReview(id, soSao, binhLuan);
            redirectAttributes.addFlashAttribute("success", "Cập nhật đánh giá thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/danhgia";
    }

    // Xóa đánh giá
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            ttaAdminDanhGiaService.deleteReview(id);
            redirectAttributes.addFlashAttribute("success", "Xóa đánh giá thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/danhgia";
    }
}
