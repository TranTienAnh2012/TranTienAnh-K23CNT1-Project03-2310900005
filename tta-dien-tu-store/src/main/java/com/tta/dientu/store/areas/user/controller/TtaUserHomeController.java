package com.tta.dientu.store.areas.user.controller;

import com.tta.dientu.store.entity.TtaBanner;
import com.tta.dientu.store.repository.TtaDanhMucRepository;
import com.tta.dientu.store.repository.TtaSanPhamRepository;
import com.tta.dientu.store.service.TtaBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class TtaUserHomeController {

    private final TtaSanPhamRepository ttaSanPhamRepository;
    private final TtaDanhMucRepository ttaDanhMucRepository;
    private final TtaBannerService ttaBannerService;
    private final com.tta.dientu.store.service.TtaDanhGiaService ttaDanhGiaService;

    @GetMapping({ "", "/", "/dashboard" })
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Trang chủ - TTA Store");
        model.addAttribute("activePage", "dashboard");

        // Lấy banner chung cho trang chủ
        List<TtaBanner> generalBanners = ttaBannerService.getGeneralBanners();
        if (!generalBanners.isEmpty()) {
            model.addAttribute("ttaBanner", generalBanners.get(0));
        }

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

        java.util.List<com.tta.dientu.store.entity.TtaSanPham> products;

        // Nếu có từ khóa tìm kiếm, ưu tiên tìm kiếm theo từ khóa
        if (keyword != null && !keyword.trim().isEmpty()) {
            products = ttaSanPhamRepository.searchByKeywordList(keyword.trim());
            model.addAttribute("keyword", keyword);
            model.addAttribute("pageTitle", "Tìm kiếm: " + keyword + " - TTA Store");
        } else {
            BigDecimal min = minPrice != null ? BigDecimal.valueOf(minPrice) : BigDecimal.ZERO;
            BigDecimal max = maxPrice != null ? BigDecimal.valueOf(maxPrice) : BigDecimal.valueOf(1000000000); // 1 tỷ

            if (categoryId != null) {
                // Lấy banner theo danh mục
                ttaDanhMucRepository.findById(categoryId).ifPresent(category -> {
                    ttaBannerService.getBannerByCategory(category).ifPresent(banner -> {
                        model.addAttribute("ttaBanner", banner);
                    });
                });

                if (minPrice != null || maxPrice != null) {
                    products = ttaSanPhamRepository.findByCategoryAndPrice(categoryId, min, max);
                } else {
                    products = ttaSanPhamRepository.findByTtaDanhMuc_TtaMaDanhMuc(categoryId);
                }
                model.addAttribute("activeCategoryId", categoryId);
            } else {
                // Lấy banner chung
                List<TtaBanner> banners = ttaBannerService.getAllActiveBanners();
                System.out.println(">>> CHECK BANNERS: Found " + banners.size() + " active banners.");
                if (!banners.isEmpty()) {
                    model.addAttribute("bannerList", banners);
                }

                if (minPrice != null || maxPrice != null) {
                    products = ttaSanPhamRepository.findByGiaBetween(min, max);
                } else {
                    products = ttaSanPhamRepository.findByTtaTrangThai(true);
                }
            }

            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
        }

        model.addAttribute("ttaSanPhams", products);

        // Tính rating cho mỗi sản phẩm
        java.util.Map<Integer, Double> productRatings = new java.util.HashMap<>();
        java.util.Map<Integer, Long> productReviewCounts = new java.util.HashMap<>();

        for (com.tta.dientu.store.entity.TtaSanPham product : products) {
            Double avgRating = ttaDanhGiaService.getAverageRating(product.getTtaMaSanPham());
            Long reviewCount = ttaDanhGiaService.getReviewCount(product.getTtaMaSanPham());
            productRatings.put(product.getTtaMaSanPham(), avgRating);
            productReviewCounts.put(product.getTtaMaSanPham(), reviewCount);
        }

        model.addAttribute("productRatings", productRatings);
        model.addAttribute("productReviewCounts", productReviewCounts);

        return "areas/user/TtaSanPham/tta-list";
    }

    @GetMapping("/san-pham/{id}")
    public String viewProductDetail(@PathVariable("id") Integer id, Model model,
            org.springframework.security.core.Authentication authentication) {
        com.tta.dientu.store.entity.TtaSanPham ttaSanPham = ttaSanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        model.addAttribute("ttaSanPham", ttaSanPham);
        model.addAttribute("pageTitle", ttaSanPham.getTtaTenSanPham() + " - TTA Store");

        // Thêm reviews
        model.addAttribute("reviews", ttaDanhGiaService.getReviewsByProduct(id));
        model.addAttribute("averageRating", ttaDanhGiaService.getAverageRating(id));
        model.addAttribute("reviewCount", ttaDanhGiaService.getReviewCount(id));

        // Kiểm tra user đã đánh giá chưa và có thể đánh giá không
        if (authentication != null && authentication.isAuthenticated()) {
            com.tta.dientu.store.areas.user.service.TtaCustomUserDetails userDetails = (com.tta.dientu.store.areas.user.service.TtaCustomUserDetails) authentication
                    .getPrincipal();
            Integer userId = userDetails.getTtaMaNguoiDung();

            boolean hasReviewed = ttaDanhGiaService.hasUserReviewed(id, userId);
            boolean hasPurchased = ttaDanhGiaService.hasUserPurchasedProduct(id, userId);

            model.addAttribute("hasReviewed", hasReviewed);
            model.addAttribute("hasPurchased", hasPurchased);
            model.addAttribute("canReview", hasPurchased && !hasReviewed);
        }

        return "areas/user/TtaSanPham/tta-detail";
    }
}
