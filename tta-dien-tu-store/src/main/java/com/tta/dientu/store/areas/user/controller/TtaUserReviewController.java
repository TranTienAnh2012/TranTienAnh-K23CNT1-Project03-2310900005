package com.tta.dientu.store.areas.user.controller;

import com.tta.dientu.store.service.TtaDanhGiaService;
import com.tta.dientu.store.areas.user.service.TtaCustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user/review")
public class TtaUserReviewController {

    @Autowired
    private TtaDanhGiaService ttaDanhGiaService;

    // Gửi đánh giá
    @PostMapping("/submit")
    public String submitReview(
            @RequestParam Integer maSanPham,
            @RequestParam Integer soSao,
            @RequestParam String binhLuan,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            // Lấy ID người dùng từ authentication
            TtaCustomUserDetails userDetails = (TtaCustomUserDetails) authentication.getPrincipal();
            Integer maNguoiDung = userDetails.getTtaMaNguoiDung();

            // Gửi đánh giá
            ttaDanhGiaService.submitReview(maSanPham, maNguoiDung, soSao, binhLuan);
            redirectAttributes.addFlashAttribute("success", "Cảm ơn bạn đã đánh giá sản phẩm!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/user/san-pham/" + maSanPham;
    }
}
