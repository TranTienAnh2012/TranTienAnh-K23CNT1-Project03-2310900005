package com.tta.dientu.store.areas.admin.controller;

import com.tta.dientu.store.areas.admin.service.TtaAdminDanhMucService;
import com.tta.dientu.store.entity.TtaBanner;
import com.tta.dientu.store.service.TtaBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/banner")
@RequiredArgsConstructor
public class TtaAdminBannerController {

    private final TtaBannerService ttaBannerService;
    private final TtaAdminDanhMucService ttaAdminDanhMucService;

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @GetMapping
    public String listBanners(Model model) {
        List<TtaBanner> banners = ttaBannerService.getAllBanners();
        model.addAttribute("ttaBanners", banners);
        return "areas/admin/TtaBanner/tta-list";
    }

    @GetMapping("/add")
    public String addBannerForm(Model model) {
        model.addAttribute("ttaBanner", new TtaBanner());
        model.addAttribute("ttaDanhMucs", ttaAdminDanhMucService.getAllDanhMuc());
        return "areas/admin/TtaBanner/tta-create";
    }

    @PostMapping("/add")
    public String addBanner(@ModelAttribute TtaBanner ttaBanner,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {
        try {
            if (!imageFile.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + fileName);
                Files.write(path, imageFile.getBytes());
                ttaBanner.setTtaHinhAnh(fileName);
            }
            if (ttaBanner.getTtaTrangThai() == null) {
                ttaBanner.setTtaTrangThai(false);
            }
            ttaBannerService.saveBanner(ttaBanner);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm banner thành công!");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi upload ảnh!");
        }
        return "redirect:/admin/banner";
    }

    @GetMapping("/edit/{id}")
    public String editBannerForm(@PathVariable("id") Integer id, Model model) {
        TtaBanner ttaBanner = ttaBannerService.getBannerById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid banner Id:" + id));
        model.addAttribute("ttaBanner", ttaBanner);
        model.addAttribute("ttaDanhMucs", ttaAdminDanhMucService.getAllDanhMuc());
        return "areas/admin/TtaBanner/tta-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateBanner(@PathVariable("id") Integer id,
            @ModelAttribute TtaBanner ttaBanner,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {
        try {
            TtaBanner existingBanner = ttaBannerService.getBannerById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid banner Id:" + id));

            existingBanner.setTtaTenBanner(ttaBanner.getTtaTenBanner());
            existingBanner.setTtaNoiDung(ttaBanner.getTtaNoiDung());
            existingBanner.setTtaTrangThai(ttaBanner.getTtaTrangThai());
            existingBanner.setTtaDanhMuc(ttaBanner.getTtaDanhMuc());

            if (!imageFile.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + fileName);
                Files.write(path, imageFile.getBytes());
                existingBanner.setTtaHinhAnh(fileName);
            }

            ttaBannerService.saveBanner(existingBanner);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật banner thành công!");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi upload ảnh!");
        }
        return "redirect:/admin/banner";
    }

    @GetMapping("/delete/{id}")
    public String deleteBanner(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        ttaBannerService.deleteBanner(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa banner thành công!");
        return "redirect:/admin/banner";
    }
}
