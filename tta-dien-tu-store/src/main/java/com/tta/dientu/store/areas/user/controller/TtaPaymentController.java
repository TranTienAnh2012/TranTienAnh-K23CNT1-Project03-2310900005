package com.tta.dientu.store.areas.user.controller;

import com.tta.dientu.store.entity.TtaPaymentSession;
import com.tta.dientu.store.service.TtaPaymentSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/user/payment")
@RequiredArgsConstructor
public class TtaPaymentController {

    private final TtaPaymentSessionService paymentSessionService;

    /**
     * Hiển thị trang thanh toán QR code cho session
     */
    @GetMapping("/session/{sessionId}")
    public String showPaymentPage(@PathVariable Integer sessionId, Model model) {
        try {
            System.out.println("=== DEBUG: Received sessionId = " + sessionId);

            TtaPaymentSession session = paymentSessionService.getSession(sessionId);

            System.out.println("=== DEBUG: Session found:");
            System.out.println("  - ID: " + session.getTtaMaSession());
            System.out.println("  - Amount: " + session.getTtaTongTien());
            System.out.println("  - Status: " + session.getTtaTrangThai());
            System.out.println("  - Expiry: " + session.getTtaNgayHetHan());
            System.out.println("  - QR URL: " + session.getTtaQrCodeUrl());

            // Check if expired
            if (session.isExpired()) {
                System.out.println("=== DEBUG: Session is EXPIRED");
                model.addAttribute("error", "Phiên thanh toán đã hết hạn");
                return "areas/user/payment/tta-payment-expired";
            }

            // Check if already paid
            if (session.isPaid()) {
                System.out.println("=== DEBUG: Session is already PAID");
                return "redirect:/user/invoice/" + session.getTtaDonHang().getTtaMaDonHang();
            }

            // Check if cancelled
            if (session.isCancelled()) {
                System.out.println("=== DEBUG: Session is CANCELLED");
                model.addAttribute("error", "Phiên thanh toán đã bị hủy");
                return "redirect:/user/cart";
            }

            model.addAttribute("paymentSession", session);
            model.addAttribute("pageTitle", "Thanh toán đơn hàng");

            System.out.println("=== DEBUG: Rendering payment page with session");

            return "areas/user/payment/tta-payment-qr";
        } catch (Exception e) {
            System.out.println("=== DEBUG: ERROR - " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "areas/user/payment/tta-payment-error";
        }
    }

    /**
     * API endpoint để kiểm tra trạng thái thanh toán (polling)
     */
    @GetMapping("/api/check-session/{sessionId}")
    @ResponseBody
    public Map<String, Object> checkSessionStatus(@PathVariable Integer sessionId) {
        return paymentSessionService.checkPaymentStatus(sessionId);
    }

    /**
     * Hủy phiên thanh toán
     */
    @PostMapping("/cancel/{sessionId}")
    public String cancelPayment(@PathVariable Integer sessionId, RedirectAttributes redirectAttributes) {
        try {
            paymentSessionService.cancelSession(sessionId);
            redirectAttributes.addFlashAttribute("message", "Đã hủy giao dịch");
            return "redirect:/user/cart";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/cart";
        }
    }

    /**
     * Trang thanh toán hết hạn
     */
    @GetMapping("/expired")
    public String paymentExpired(Model model) {
        model.addAttribute("pageTitle", "Thanh toán hết hạn");
        return "areas/user/payment/tta-payment-expired";
    }
}
