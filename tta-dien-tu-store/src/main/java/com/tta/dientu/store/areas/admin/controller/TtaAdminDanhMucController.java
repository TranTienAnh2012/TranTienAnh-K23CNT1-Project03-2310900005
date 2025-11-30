package com.tta.dientu.store.areas.admin.controller;

import com.tta.dientu.store.areas.admin.service.TtaAdminDanhMucService;
import com.tta.dientu.store.entity.TtaDanhMuc;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/danhmuc")
@RequiredArgsConstructor
public class TtaAdminDanhMucController {

    private final TtaAdminDanhMucService ttaAdminDanhMucService;

    // Danh sách danh mục
    @GetMapping("")
    public String listDanhMuc(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ttaMaDanhMuc") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String keyword,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TtaDanhMuc> ttaDanhMucPage;
        List<TtaDanhMuc> ttaDanhMucList;
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Với danh mục, chúng ta sẽ lấy tất cả và filter ở service
            ttaDanhMucList = ttaAdminDanhMucService.searchDanhMuc(keyword);
            // Tạo page từ list để phân trang vẫn hoạt động
            ttaDanhMucPage = new PageImpl<>(ttaDanhMucList, pageable, ttaDanhMucList.size());
            model.addAttribute("keyword", keyword);
        } else {
            ttaDanhMucPage = ttaAdminDanhMucService.getAllDanhMuc(pageable);
            ttaDanhMucList = ttaDanhMucPage.getContent();
        }

        // Thêm số sản phẩm cho mỗi danh mục
        Map<Integer, Long> ttaSoSanPhamMap = ttaDanhMucList.stream()
                .collect(Collectors.toMap(
                        TtaDanhMuc::getTtaMaDanhMuc,
                        ttaDanhMuc -> ttaAdminDanhMucService.countSanPhamByDanhMuc(ttaDanhMuc.getTtaMaDanhMuc())));
        model.addAttribute("ttaDanhMucList", ttaDanhMucList);
        model.addAttribute("ttaSoSanPhamMap", ttaSoSanPhamMap);

        model.addAttribute("pageTitle", "TTA Admin - Quản lý Danh mục");
        model.addAttribute("activePage", "danhmuc");
        model.addAttribute("ttaDanhMucPage", ttaDanhMucPage);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        return "areas/admin/TtaDanhMuc/tta-list";
    }

    // Form thêm danh mục
    @GetMapping("/them")
    public String showThemDanhMucForm(Model model) {
        model.addAttribute("pageTitle", "TTA Admin - Thêm Danh mục");
        model.addAttribute("activePage", "danhmuc");
        model.addAttribute("ttaDanhMuc", new TtaDanhMuc());
        return "areas/admin/TtaDanhMuc/tta-create";
    }

    // Xử lý thêm danh mục
    @PostMapping("/them")
    public String themDanhMuc(@ModelAttribute TtaDanhMuc ttaDanhMuc,
            RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra tên danh mục đã tồn tại chưa
            if (ttaAdminDanhMucService.isTenDanhMucExists(ttaDanhMuc.getTtaTenDanhMuc(), null)) {
                redirectAttributes.addFlashAttribute("error", "Tên danh mục đã tồn tại");
                return "redirect:/admin/danhmuc/them";
            }

            ttaAdminDanhMucService.saveDanhMuc(ttaDanhMuc);
            redirectAttributes.addFlashAttribute("success", "Thêm danh mục thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm danh mục: " + e.getMessage());
            return "redirect:/admin/danhmuc/them";
        }
        return "redirect:/admin/danhmuc";
    }

    // Form sửa danh mục
    @GetMapping("/sua/{id}")
    public String showSuaDanhMucForm(@PathVariable Integer id, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<TtaDanhMuc> ttaDanhMucOpt = ttaAdminDanhMucService.getDanhMucById(id);
        if (ttaDanhMucOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy danh mục");
            return "redirect:/admin/danhmuc";
        }

        // Lấy số sản phẩm trong danh mục
        long ttaSoSanPham = ttaAdminDanhMucService.countSanPhamByDanhMuc(id);

        model.addAttribute("pageTitle", "TTA Admin - Sửa Danh mục");
        model.addAttribute("activePage", "danhmuc");
        model.addAttribute("ttaDanhMuc", ttaDanhMucOpt.get());
        model.addAttribute("ttaSoSanPham", ttaSoSanPham);
        return "areas/admin/TtaDanhMuc/tta-edit";
    }

    // Xử lý sửa danh mục
    @PostMapping("/sua/{id}")
    public String suaDanhMuc(@PathVariable Integer id,
            @ModelAttribute TtaDanhMuc ttaDanhMuc,
            RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra tên danh mục đã tồn tại chưa (trừ danh mục hiện tại)
            if (ttaAdminDanhMucService.isTenDanhMucExists(ttaDanhMuc.getTtaTenDanhMuc(), id)) {
                redirectAttributes.addFlashAttribute("error", "Tên danh mục đã tồn tại");
                return "redirect:/admin/danhmuc/sua/" + id;
            }

            ttaDanhMuc.setTtaMaDanhMuc(id);
            ttaAdminDanhMucService.saveDanhMuc(ttaDanhMuc);
            redirectAttributes.addFlashAttribute("success", "Cập nhật danh mục thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật danh mục: " + e.getMessage());
            return "redirect:/admin/danhmuc/sua/" + id;
        }
        return "redirect:/admin/danhmuc";
    }

    // Xóa danh mục
    @PostMapping("/xoa/{id}")
    public String xoaDanhMuc(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            boolean deleted = ttaAdminDanhMucService.deleteDanhMuc(id);
            if (deleted) {
                redirectAttributes.addFlashAttribute("success", "Xóa danh mục thành công");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy danh mục để xóa");
            }
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa danh mục: " + e.getMessage());
        }
        return "redirect:/admin/danhmuc";
    }

    // API lấy danh sách danh mục (dùng cho select options)
    @GetMapping("/api")
    @ResponseBody
    public List<TtaDanhMuc> getAllDanhMucApi() {
        return ttaAdminDanhMucService.getAllDanhMuc();
    }

    // Kiểm tra tên danh mục (dùng cho AJAX validation)
    @GetMapping("/kiemtra-ten")
    @ResponseBody
    public String kiemTraTenDanhMuc(@RequestParam String ttaTenDanhMuc,
            @RequestParam(required = false) Integer excludeId) {
        boolean exists = ttaAdminDanhMucService.isTenDanhMucExists(ttaTenDanhMuc, excludeId);
        return exists ? "EXISTS" : "OK";
    }
}
