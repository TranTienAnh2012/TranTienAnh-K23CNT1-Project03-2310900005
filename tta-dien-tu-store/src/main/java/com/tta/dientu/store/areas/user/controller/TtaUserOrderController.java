package com.tta.dientu.store.areas.user.controller;

import com.tta.dientu.store.entity.TtaDonHang;
import com.tta.dientu.store.entity.TtaChiTietDonHang;
import com.tta.dientu.store.repository.TtaChiTietDonHangRepository;
import com.tta.dientu.store.service.TtaUserOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class TtaUserOrderController {

    private final TtaUserOrderService userOrderService;
    private final TtaChiTietDonHangRepository ttaChiTietDonHangRepository;

    /**
     * Hiển thị danh sách đơn hàng của người dùng
     */
    @GetMapping("/user/orders")
    public String listOrders(Model model) {
        List<TtaDonHang> orders = userOrderService.getMyOrders();
        model.addAttribute("orders", orders);
        return "areas/user/orders/tta-orders";
    }

    /**
     * Hiển thị chi tiết đơn hàng
     */
    @GetMapping("/user/orders/{id}")
    public String orderDetail(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<TtaDonHang> orderOpt = userOrderService.getOrderDetail(id);

        if (orderOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "Không tìm thấy đơn hàng hoặc bạn không có quyền xem đơn hàng này.");
            return "redirect:/user/orders";
        }

        TtaDonHang order = orderOpt.get();
        model.addAttribute("order", order);
        model.addAttribute("canCancel", userOrderService.canCancelOrder(order));

        return "areas/user/orders/tta-order-detail";
    }

    /**
     * Hủy đơn hàng
     */
    @PostMapping("/user/orders/{id}/cancel")
    public String cancelOrder(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        boolean success = userOrderService.cancelOrder(id);

        if (success) {
            redirectAttributes.addFlashAttribute("success", "Đơn hàng đã được hủy thành công.");
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "Không thể hủy đơn hàng. Vui lòng kiểm tra lại trạng thái đơn hàng.");
        }

        return "redirect:/user/orders";
    }

    /**
     * Hiển thị hóa đơn
     */
    @GetMapping("/user/invoice/{id}")
    public String viewInvoice(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<TtaDonHang> orderOpt = userOrderService.getOrderDetail(id);

        if (orderOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "Không tìm thấy đơn hàng hoặc bạn không có quyền xem đơn hàng này.");
            return "redirect:/user/orders";
        }

        TtaDonHang order = orderOpt.get();
        List<TtaChiTietDonHang> orderDetails = ttaChiTietDonHangRepository.findByTtaDonHang_TtaMaDonHang(id);

        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("pageTitle", "Hóa đơn #" + id);

        return "areas/user/order/tta-invoice";
    }
}
