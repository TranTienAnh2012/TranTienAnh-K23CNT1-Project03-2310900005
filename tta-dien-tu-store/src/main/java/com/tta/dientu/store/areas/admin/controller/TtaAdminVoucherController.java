package com.tta.dientu.store.areas.admin.controller;

import com.tta.dientu.store.entity.TtaVoucher;
import com.tta.dientu.store.enums.TtaDiscountType;
import com.tta.dientu.store.enums.TtaVoucherStatus;
import com.tta.dientu.store.service.TtaVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/khuyenmai")
@RequiredArgsConstructor
public class TtaAdminVoucherController {

    private final TtaVoucherService voucherService;

    @GetMapping
    public String list(Model model) {
        List<TtaVoucher> list = voucherService.getAllVouchers();
        // Sắp xếp giảm dần theo ngày bắt đầu (nếu có id thì sắp theo id giảm dần để mới
        // nhất lên đầu)
        list.sort((a, b) -> b.getTtaId().compareTo(a.getTtaId()));
        model.addAttribute("listVoucher", list);
        model.addAttribute("pageTitle", "Quản lý Voucher");
        return "areas/admin/TtaKhuyenMai/tta-list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("voucher", new TtaVoucher());
        model.addAttribute("discountTypes", TtaDiscountType.values());
        model.addAttribute("statuses", TtaVoucherStatus.values());
        model.addAttribute("pageTitle", "Thêm mới Voucher");
        return "areas/admin/TtaKhuyenMai/tta-create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("voucher") TtaVoucher voucher, RedirectAttributes redirectAttributes) {
        try {
            // Basic validation handled in Service or verify logic here if needed
            voucherService.createVoucher(voucher);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm voucher thành công!");
            return "redirect:/admin/khuyenmai";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/admin/khuyenmai/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            TtaVoucher voucher = voucherService.getVoucherById(id);
            model.addAttribute("voucher", voucher);
            model.addAttribute("discountTypes", TtaDiscountType.values());
            model.addAttribute("statuses", TtaVoucherStatus.values());
            model.addAttribute("pageTitle", "Chỉnh sửa Voucher");
            return "areas/admin/TtaKhuyenMai/tta-edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy voucher!");
            return "redirect:/admin/khuyenmai";
        }
    }

    @PostMapping("/edit")
    public String update(@ModelAttribute("voucher") TtaVoucher voucher, RedirectAttributes redirectAttributes) {
        try {
            voucherService.updateVoucher(voucher.getTtaId(), voucher);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật voucher thành công!");
            return "redirect:/admin/khuyenmai";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/admin/khuyenmai/edit/" + voucher.getTtaId();
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            voucherService.deleteVoucher(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa voucher thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/khuyenmai";
    }
}
