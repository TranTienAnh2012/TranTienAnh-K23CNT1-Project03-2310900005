package com.tta.dientu.store.areas.user.controller;

import com.tta.dientu.store.service.TtaCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user/cart")
@RequiredArgsConstructor
public class TtaCartController {
    private final TtaCartService ttaCartService;

    private final com.tta.dientu.store.repository.TtaQuanTriVienRepository ttaQuanTriVienRepository;

    @GetMapping
    public String viewCart(Model model) {
        model.addAttribute("ttaCartItems", ttaCartService.getCartItems());
        model.addAttribute("total", ttaCartService.getTotal());
        model.addAttribute("pageTitle", "Giỏ hàng - TTA Store");
        return "areas/user/cart/tta-list";
    }

    @GetMapping("/add/{id}")
    public String addToCart(@PathVariable("id") Integer id,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity) {
        try {
            ttaCartService.addToCart(id, quantity);
        } catch (RuntimeException e) {
            return "redirect:/account/login";
        }
        return "redirect:/user/cart";
    }

    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable("id") Integer id) {
        ttaCartService.removeFromCart(id);
        return "redirect:/user/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam("id") Integer id, @RequestParam("quantity") int quantity) {
        ttaCartService.updateQuantity(id, quantity);
        return "redirect:/user/cart";
    }

    @GetMapping("/checkout")
    public String checkout(Model model) {
        if (ttaCartService.getCartItems().isEmpty()) {
            return "redirect:/user/cart";
        }

        // Lấy thông tin user hiện tại để điền vào form
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            String email = authentication.getName();
            ttaQuanTriVienRepository.findByTtaEmail(email).ifPresent(user -> {
                model.addAttribute("user", user);
            });
        }

        model.addAttribute("ttaCartItems", ttaCartService.getCartItems());
        model.addAttribute("total", ttaCartService.getTotal());
        model.addAttribute("pageTitle", "Thanh toán - TTA Store");
        return "areas/user/cart/tta-checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(@RequestParam("hoTen") String hoTen,
            @RequestParam("soDienThoai") String soDienThoai,
            @RequestParam("diaChi") String diaChi,
            @RequestParam("email") String email,
            @RequestParam(value = "ghiChu", required = false) String ghiChu,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            ttaCartService.checkout(hoTen, soDienThoai, diaChi, email, ghiChu);
            redirectAttributes.addFlashAttribute("message", "Đặt hàng thành công!");
            return "redirect:/user/cart"; // Hoặc trang thông báo thành công riêng
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/cart/checkout";
        }
    }
}
