package com.tta.dientu.store.areas.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class TtaUserHomeController {

    private final com.tta.dientu.store.repository.TtaSanPhamRepository ttaSanPhamRepository;
    private final com.tta.dientu.store.repository.TtaDanhMucRepository ttaDanhMucRepository;

    @GetMapping({ "", "/", "/dashboard" })
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Trang chủ - TTA Store");
        model.addAttribute("activePage", "dashboard");
        return "areas/user/home/tta-dashboard";
    }

    @GetMapping("/san-pham")
    public String listProducts(@RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String keyword,
            Model model) {
        model.addAttribute("pageTitle", "Sản phẩm - TTA Store");
        model.addAttribute("activePage", "san-pham");

        // Lấy danh sách danh mục để hiển thị filter
        model.addAttribute("ttaDanhMucs", ttaDanhMucRepository.findAllByOrderByTtaTenDanhMucAsc());

        // Nếu có từ khóa tìm kiếm, ưu tiên tìm kiếm theo từ khóa
        if (keyword != null && !keyword.trim().isEmpty()) {
            model.addAttribute("ttaSanPhams", ttaSanPhamRepository.searchByKeywordList(keyword.trim()));
            model.addAttribute("keyword", keyword);
            model.addAttribute("pageTitle", "Tìm kiếm: " + keyword + " - TTA Store");
        } else {
            BigDecimal min = minPrice != null ? BigDecimal.valueOf(minPrice) : BigDecimal.ZERO;
            BigDecimal max = maxPrice != null ? BigDecimal.valueOf(maxPrice) : BigDecimal.valueOf(1000000000); // 1 tỷ

            if (categoryId != null) {
                if (minPrice != null || maxPrice != null) {
                    model.addAttribute("ttaSanPhams",
                            ttaSanPhamRepository.findByCategoryAndPrice(categoryId, min, max));
                } else {
                    model.addAttribute("ttaSanPhams", ttaSanPhamRepository.findByTtaDanhMuc_TtaMaDanhMuc(categoryId));
                }
                model.addAttribute("activeCategoryId", categoryId);
            } else {
                if (minPrice != null || maxPrice != null) {
                    model.addAttribute("ttaSanPhams", ttaSanPhamRepository.findByGiaBetween(min, max));
                } else {
                    model.addAttribute("ttaSanPhams", ttaSanPhamRepository.findByTtaTrangThai(true));
                }
            }

            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
        }

        return "areas/user/TtaSanPham/tta-list";
    }

    @GetMapping("/san-pham/{id}")
    public String viewProductDetail(@PathVariable("id") Integer id, Model model) {
        com.tta.dientu.store.entity.TtaSanPham ttaSanPham = ttaSanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        model.addAttribute("ttaSanPham", ttaSanPham);
        model.addAttribute("pageTitle", ttaSanPham.getTtaTenSanPham() + " - TTA Store");
        return "areas/user/TtaSanPham/tta-detail";
    }
}
