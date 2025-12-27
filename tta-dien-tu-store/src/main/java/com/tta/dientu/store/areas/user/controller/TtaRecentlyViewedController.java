package com.tta.dientu.store.areas.user.controller;

import com.tta.dientu.store.areas.user.service.TtaCustomUserDetails;
import com.tta.dientu.store.entity.TtaSanPham;
import com.tta.dientu.store.service.TtaSanPhamDaXemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/user/recently-viewed")
@RequiredArgsConstructor
public class TtaRecentlyViewedController {

    private final TtaSanPhamDaXemService ttaSanPhamDaXemService;

    /**
     * Hiển thị trang danh sách sản phẩm đã xem
     */
    @GetMapping
    public String showRecentlyViewed(Model model, Authentication authentication) {
        model.addAttribute("pageTitle", "Sản phẩm đã xem - TTA Store");
        model.addAttribute("activePage", "recently-viewed");

        if (authentication != null && authentication.isAuthenticated()) {
            TtaCustomUserDetails userDetails = (TtaCustomUserDetails) authentication.getPrincipal();
            Integer userId = userDetails.getTtaMaNguoiDung();

            // Lấy tất cả sản phẩm đã xem (giới hạn 50)
            List<TtaSanPham> recentlyViewedProducts = ttaSanPhamDaXemService.getRecentlyViewedProducts(userId, 50);
            model.addAttribute("recentlyViewedProducts", recentlyViewedProducts);
            model.addAttribute("totalViewed", recentlyViewedProducts.size());
        } else {
            // Người dùng chưa đăng nhập
            model.addAttribute("recentlyViewedProducts", List.of());
            model.addAttribute("totalViewed", 0);
        }

        return "areas/user/recently-viewed/tta-recently-viewed";
    }

    /**
     * Xóa toàn bộ lịch sử xem sản phẩm
     */
    @PostMapping("/clear")
    public String clearHistory(Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication != null && authentication.isAuthenticated()) {
            TtaCustomUserDetails userDetails = (TtaCustomUserDetails) authentication.getPrincipal();
            Integer userId = userDetails.getTtaMaNguoiDung();

            ttaSanPhamDaXemService.clearViewingHistory(userId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa lịch sử xem sản phẩm thành công!");
        }

        return "redirect:/user/recently-viewed";
    }
}
