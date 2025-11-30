package com.tta.dientu.store.areas.admin.controller;

import com.tta.dientu.store.areas.admin.service.TtaAdminSanPhamService;
import com.tta.dientu.store.entity.TtaSanPham;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Controller
@RequestMapping("/admin/sanpham")
@RequiredArgsConstructor
public class TtaAdminSanPhamController {

    private final TtaAdminSanPhamService ttaAdminSanPhamService;

    // Danh sách sản phẩm phân trang
    @GetMapping("")
    public String listSanPham(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "ttaMaSanPham") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String keyword,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TtaSanPham> ttaSanPhamPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            ttaSanPhamPage = ttaAdminSanPhamService.searchSanPham(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            ttaSanPhamPage = ttaAdminSanPhamService.getAllSanPham(pageable);
        }

        model.addAttribute("pageTitle", "TTA Admin - Quản lý Sản phẩm");
        model.addAttribute("activePage", "TtaSanPham");
        model.addAttribute("ttaSanPhamPage", ttaSanPhamPage);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        return "areas/admin/TtaSanPham/tta-list";
    }

    // Form thêm sản phẩm
    @GetMapping("/them")
    public String showThemSanPhamForm(Model model) {
        model.addAttribute("pageTitle", "TTA Admin - Thêm Sản phẩm");
        model.addAttribute("activePage", "TtaSanPham");
        model.addAttribute("ttaSanPham", new TtaSanPham());
        model.addAttribute("ttaDanhMucList", ttaAdminSanPhamService.getAllDanhMuc());
        return "areas/admin/TtaSanPham/tta-create";
    }

    // Xử lý thêm sản phẩm
    @PostMapping("/them")
    public String themSanPham(@ModelAttribute TtaSanPham ttaSanPham,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra tên sản phẩm đã tồn tại chưa
            if (ttaAdminSanPhamService.isTenSanPhamExists(ttaSanPham.getTtaTenSanPham(), null)) {
                redirectAttributes.addFlashAttribute("error", "Tên sản phẩm đã tồn tại");
                return "redirect:/admin/TtaSanPham/them";
            }

            // Xử lý upload ảnh
            if (!imageFile.isEmpty()) {
                String fileName = saveFile(imageFile);
                ttaSanPham.setTtaHinhAnh(fileName);
            }

            ttaAdminSanPhamService.saveSanPham(ttaSanPham);
            redirectAttributes.addFlashAttribute("success", "Thêm sản phẩm thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm sản phẩm: " + e.getMessage());
            return "redirect:/admin/TtaSanPham/them";
        }
        return "redirect:/admin/TtaSanPham";
    }

    // Form sửa sản phẩm
    @GetMapping("/sua/{id}")
    public String showSuaSanPhamForm(@PathVariable Integer id, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<TtaSanPham> ttaSanPhamOpt = ttaAdminSanPhamService.getSanPhamById(id);
        if (ttaSanPhamOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm");
            return "redirect:/admin/TtaSanPham";
        }

        model.addAttribute("pageTitle", "TTA Admin - Sửa Sản phẩm");
        model.addAttribute("activePage", "TtaSanPham");
        model.addAttribute("ttaSanPham", ttaSanPhamOpt.get());
        model.addAttribute("ttaDanhMucList", ttaAdminSanPhamService.getAllDanhMuc());
        return "areas/admin/TtaSanPham/tta-edit";
    }

    // Xử lý sửa sản phẩm
    @PostMapping("/sua/{id}")
    public String suaSanPham(@PathVariable Integer id,
            @ModelAttribute TtaSanPham ttaSanPham,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra tên sản phẩm đã tồn tại chưa (trừ sản phẩm hiện tại)
            if (ttaAdminSanPhamService.isTenSanPhamExists(ttaSanPham.getTtaTenSanPham(), id)) {
                redirectAttributes.addFlashAttribute("error", "Tên sản phẩm đã tồn tại");
                return "redirect:/admin/TtaSanPham/sua/" + id;
            }

            // Xử lý upload ảnh
            if (!imageFile.isEmpty()) {
                String fileName = saveFile(imageFile);
                ttaSanPham.setTtaHinhAnh(fileName);
            } else {
                // Giữ lại ảnh cũ nếu không upload ảnh mới
                Optional<TtaSanPham> oldSanPham = ttaAdminSanPhamService.getSanPhamById(id);
                oldSanPham.ifPresent(p -> ttaSanPham.setTtaHinhAnh(p.getTtaHinhAnh()));
            }

            ttaSanPham.setTtaMaSanPham(id);
            ttaAdminSanPhamService.saveSanPham(ttaSanPham);
            redirectAttributes.addFlashAttribute("success", "Cập nhật sản phẩm thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
            return "redirect:/admin/TtaSanPham/sua/" + id;
        }
        return "redirect:/admin/TtaSanPham";
    }

    // Xóa sản phẩm
    @PostMapping("/xoa/{id}")
    public String xoaSanPham(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            boolean deleted = ttaAdminSanPhamService.deleteSanPham(id);
            if (deleted) {
                redirectAttributes.addFlashAttribute("success", "Xóa sản phẩm thành công");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm để xóa");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa sản phẩm: " + e.getMessage());
        }
        return "redirect:/admin/TtaSanPham";
    }

    // Cập nhật trạng thái sản phẩm
    @PostMapping("/trangthai/{id}")
    public String updateTrangThai(@PathVariable Integer id,
            @RequestParam Boolean ttaTrangThai,
            RedirectAttributes redirectAttributes) {
        try {
            boolean updated = ttaAdminSanPhamService.updateTrangThai(id, ttaTrangThai);
            if (updated) {
                redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái thành công");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật trạng thái");
        }
        return "redirect:/admin/TtaSanPham";
    }

    // Helper method để lưu file
    private String saveFile(MultipartFile file) throws IOException {
        String uploadDir = "src/main/resources/static/uploads/";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        try (java.io.InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        return fileName;
    }
}