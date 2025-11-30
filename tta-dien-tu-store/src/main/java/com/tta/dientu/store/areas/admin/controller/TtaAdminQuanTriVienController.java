package com.tta.dientu.store.areas.admin.controller;

import com.tta.dientu.store.areas.admin.service.TtaAdminQuanTriVienService;
import com.tta.dientu.store.entity.TtaQuanTriVien;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/admin/quan-tri-vien")
@RequiredArgsConstructor
public class TtaAdminQuanTriVienController {

    private final TtaAdminQuanTriVienService ttaAdminQuanTriVienService;

    @GetMapping("")
    public String listQuanTriVien(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ttaMaNguoiDung") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String keyword,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TtaQuanTriVien> ttaQtvPage = ttaAdminQuanTriVienService.getQuanTriVienPage(pageable, keyword);

        model.addAttribute("pageTitle", "TTA Admin - Quản trị viên");
        model.addAttribute("activePage", "TtaQuanTriVien");
        model.addAttribute("ttaQtvPage", ttaQtvPage);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);

        return "areas/admin/TtaQuanTriVien/tta-list";
    }

    @GetMapping("/them")
    public String showCreateForm(Model model) {
        model.addAttribute("pageTitle", "TTA Admin - Thêm quản trị viên");
        model.addAttribute("activePage", "TtaQuanTriVien");
        model.addAttribute("ttaQuanTriVien", new TtaQuanTriVien());
        return "areas/admin/TtaQuanTriVien/tta-create";
    }

    @PostMapping("/them")
    public String createQuanTriVien(@ModelAttribute TtaQuanTriVien ttaQuanTriVien,
            RedirectAttributes redirectAttributes) {
        try {
            if (ttaAdminQuanTriVienService.isEmailExists(ttaQuanTriVien.getTtaEmail(), null)) {
                redirectAttributes.addFlashAttribute("error", "Email đã được sử dụng");
                return "redirect:/admin/quan-tri-vien/them";
            }

            if (ttaQuanTriVien.getTtaNgayDangKy() == null) {
                ttaQuanTriVien.setTtaNgayDangKy(LocalDateTime.now());
            }

            ttaAdminQuanTriVienService.saveQuanTriVien(ttaQuanTriVien);
            redirectAttributes.addFlashAttribute("success", "Thêm quản trị viên thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm quản trị viên: " + e.getMessage());
            return "redirect:/admin/quan-tri-vien/them";
        }

        return "redirect:/admin/quan-tri-vien";
    }

    @GetMapping("/sua/{id}")
    public String showEditForm(@PathVariable Integer id,
            Model model,
            RedirectAttributes redirectAttributes) {
        Optional<TtaQuanTriVien> ttaQuanTriVienOpt = ttaAdminQuanTriVienService.getQuanTriVienById(id);
        if (ttaQuanTriVienOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy quản trị viên");
            return "redirect:/admin/quan-tri-vien";
        }

        model.addAttribute("pageTitle", "TTA Admin - Sửa quản trị viên");
        model.addAttribute("activePage", "TtaQuanTriVien");
        model.addAttribute("ttaQuanTriVien", ttaQuanTriVienOpt.get());
        return "areas/admin/TtaQuanTriVien/tta-edit";
    }

    @PostMapping("/sua/{id}")
    public String updateQuanTriVien(@PathVariable Integer id,
            @ModelAttribute TtaQuanTriVien ttaQuanTriVien,
            RedirectAttributes redirectAttributes) {
        try {
            Optional<TtaQuanTriVien> existingOpt = ttaAdminQuanTriVienService.getQuanTriVienById(id);
            if (existingOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy quản trị viên");
                return "redirect:/admin/quan-tri-vien";
            }

            if (ttaAdminQuanTriVienService.isEmailExists(ttaQuanTriVien.getTtaEmail(), id)) {
                redirectAttributes.addFlashAttribute("error", "Email đã được sử dụng");
                return "redirect:/admin/quan-tri-vien/sua/" + id;
            }

            TtaQuanTriVien existing = existingOpt.get();
            existing.setTtaHoTen(ttaQuanTriVien.getTtaHoTen());
            existing.setTtaEmail(ttaQuanTriVien.getTtaEmail());
            existing.setTtaVaiTro(ttaQuanTriVien.getTtaVaiTro());

            if (ttaQuanTriVien.getTtaMatKhau() != null && !ttaQuanTriVien.getTtaMatKhau().trim().isEmpty()) {
                existing.setTtaMatKhau(ttaQuanTriVien.getTtaMatKhau());
            }

            ttaAdminQuanTriVienService.saveQuanTriVien(existing);
            redirectAttributes.addFlashAttribute("success", "Cập nhật quản trị viên thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật quản trị viên: " + e.getMessage());
            return "redirect:/admin/quan-tri-vien/sua/" + id;
        }

        return "redirect:/admin/quan-tri-vien";
    }

    @PostMapping("/xoa/{id}")
    public String deleteQuanTriVien(@PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        if (ttaAdminQuanTriVienService.deleteQuanTriVien(id)) {
            redirectAttributes.addFlashAttribute("success", "Xóa quản trị viên thành công");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy quản trị viên để xóa");
        }
        return "redirect:/admin/quan-tri-vien";
    }
}
