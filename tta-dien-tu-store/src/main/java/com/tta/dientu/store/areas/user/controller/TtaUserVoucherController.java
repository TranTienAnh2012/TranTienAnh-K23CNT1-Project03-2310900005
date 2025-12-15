package com.tta.dientu.store.areas.user.controller;

import com.tta.dientu.store.areas.user.service.TtaCustomUserDetails;
import com.tta.dientu.store.entity.TtaUserVoucher;
import com.tta.dientu.store.entity.TtaVoucher;
import com.tta.dientu.store.service.TtaUserVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * User Voucher Controller - Updated for new simplified voucher structure
 */
@Controller
@RequestMapping("/user/vouchers")
@RequiredArgsConstructor
public class TtaUserVoucherController {

    private final TtaUserVoucherService userVoucherService;

    /**
     * Display user's vouchers page
     */
    @GetMapping
    public String myVouchers(@AuthenticationPrincipal TtaCustomUserDetails userDetails, Model model) {
        model.addAttribute("pageTitle", "Voucher của tôi - TTA Store");
        model.addAttribute("activePage", "vouchers");

        Integer userId = userDetails.getTtaMaNguoiDung();

        // Get user's claimed vouchers
        List<TtaUserVoucher> myVouchers = userVoucherService.getMyVouchers(userId);
        model.addAttribute("myVouchers", myVouchers);

        // Get available vouchers that user can claim
        List<TtaVoucher> availableVouchers = userVoucherService.getAvailableVouchers();
        model.addAttribute("availableVouchers", availableVouchers);

        return "areas/user/vouchers/tta-vouchers";
    }

    /**
     * Claim a voucher
     */
    @PostMapping("/claim/{id}")
    public String claimVoucher(@PathVariable Integer id,
            @AuthenticationPrincipal TtaCustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            Integer userId = userDetails.getTtaMaNguoiDung();
            userVoucherService.claimVoucherById(userId, id);
            redirectAttributes.addFlashAttribute("success", "Nhận voucher thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/user/vouchers";
    }

    /**
     * Claim voucher by code
     */
    @PostMapping("/claim-code")
    public String claimVoucherByCode(@RequestParam String voucherCode,
            @AuthenticationPrincipal TtaCustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            Integer userId = userDetails.getTtaMaNguoiDung();
            userVoucherService.claimVoucher(userId, voucherCode);
            redirectAttributes.addFlashAttribute("success", "Nhận voucher thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/user/vouchers";
    }
}
