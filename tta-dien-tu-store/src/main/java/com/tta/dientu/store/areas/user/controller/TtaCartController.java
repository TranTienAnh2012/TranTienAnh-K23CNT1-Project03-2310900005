package com.tta.dientu.store.areas.user.controller;

import com.tta.dientu.store.service.TtaCartService;
import com.tta.dientu.store.entity.TtaSanPham;
import com.tta.dientu.store.repository.TtaSanPhamRepository;
import java.math.BigDecimal;
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
    private final TtaSanPhamRepository ttaSanPhamRepository;
    private final com.tta.dientu.store.service.TtaUserVoucherService ttaUserVoucherService;

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
                // Get valid vouchers
                model.addAttribute("availableVouchers",
                        ttaUserVoucherService.getMyValidVouchers(user.getTtaMaNguoiDung()));
            });
        }

        model.addAttribute("ttaCartItems", ttaCartService.getCartItems());
        model.addAttribute("total", ttaCartService.getTotal());
        model.addAttribute("pageTitle", "Thanh toán - TTA Store");
        return "areas/user/cart/tta-checkout";
    }

    // Validate voucher endpoint
    @GetMapping("/validate-voucher")
    @ResponseBody
    public java.util.Map<String, Object> validateVoucher(@RequestParam("code") String code,
            @RequestParam("total") BigDecimal total) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        try {
            // Get current user
            org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập để sử dụng voucher");
                return response;
            }
            String email = authentication.getName();
            // Get user ID (Need to inject repository or service to get ID)
            com.tta.dientu.store.entity.TtaQuanTriVien user = ttaQuanTriVienRepository.findByTtaEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Use functionality from CartService or UserVoucherService (need to update
            // Service first ideally, but doing
            // validation here for now)
            // Ideally validation should be in Service. Assuming CartService will be updated
            // to handle this via delegation
            BigDecimal discount = ttaCartService.calculateVoucherDiscount(user.getTtaMaNguoiDung(), code, total);
            BigDecimal newTotal = total.subtract(discount);
            if (newTotal.compareTo(BigDecimal.ZERO) < 0)
                newTotal = BigDecimal.ZERO;

            response.put("success", true);
            response.put("discount", discount);
            response.put("newTotal", newTotal);
            response.put("message", "Áp dụng voucher thành công!");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

    @PostMapping("/checkout")
    public String processCheckout(@RequestParam("hoTen") String hoTen,
            @RequestParam("soDienThoai") String soDienThoai,
            @RequestParam("diaChi") String diaChi,
            @RequestParam("email") String email,
            @RequestParam(value = "ghiChu", required = false) String ghiChu,
            @RequestParam(value = "voucherCode", required = false) String voucherCode,
            @RequestParam(value = "paymentMethod", defaultValue = "cod") String paymentMethod,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            com.tta.dientu.store.entity.TtaDonHang donHang = ttaCartService.checkout(hoTen, soDienThoai, diaChi, email,
                    ghiChu, voucherCode);

            if ("zalopay".equals(paymentMethod)) {
                // ZaloPay removed
                redirectAttributes.addFlashAttribute("error", "Phương thức thanh toán này hiện chưa được hỗ trợ.");
                return "redirect:/user/cart/checkout";
            }

            redirectAttributes.addFlashAttribute("message", "Đặt hàng thành công!");
            return "redirect:/user/invoice/" + donHang.getTtaMaDonHang();
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/cart/checkout";
        }
    }

    // Direct checkout for Buy Now feature
    @GetMapping("/checkout/direct")
    public String directCheckout(@RequestParam("productId") Integer productId,
            @RequestParam("quantity") int quantity,
            Model model) {
        // Load product
        TtaSanPham product = ttaSanPhamRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        // Calculate total
        BigDecimal total = product.getTtaGia().multiply(BigDecimal.valueOf(quantity));

        // Get current user info
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            String email = authentication.getName();
            ttaQuanTriVienRepository.findByTtaEmail(email).ifPresent(user -> {
                model.addAttribute("user", user);
                // Get valid vouchers
                model.addAttribute("availableVouchers",
                        ttaUserVoucherService.getMyValidVouchers(user.getTtaMaNguoiDung()));
            });
        }

        model.addAttribute("product", product);
        model.addAttribute("quantity", quantity);
        model.addAttribute("total", total);
        model.addAttribute("pageTitle", "Thanh toán - TTA Store");
        return "areas/user/cart/tta-checkout-direct";
    }

    @PostMapping("/checkout/direct")
    public String processDirectCheckout(@RequestParam("productId") Integer productId,
            @RequestParam("quantity") int quantity,
            @RequestParam("hoTen") String hoTen,
            @RequestParam("soDienThoai") String soDienThoai,
            @RequestParam("diaChi") String diaChi,
            @RequestParam(value = "ghiChu", required = false) String ghiChu,
            @RequestParam(value = "voucherCode", required = false) String voucherCode,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            // Create order directly
            Integer orderId = ttaCartService.createDirectOrder(productId, quantity, hoTen, soDienThoai, diaChi, ghiChu,
                    voucherCode);
            redirectAttributes.addFlashAttribute("message", "Đặt hàng thành công!");
            return "redirect:/user/invoice/" + orderId;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/cart/checkout/direct?productId=" + productId + "&quantity=" + quantity;
        }
    }
}
