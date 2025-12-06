package com.tta.dientu.store.service;

import com.tta.dientu.store.entity.TtaDonHang;
import com.tta.dientu.store.entity.TtaQuanTriVien;
import com.tta.dientu.store.enums.TtaTrangThaiDonHang;
import com.tta.dientu.store.repository.TtaDonHangRepository;
import com.tta.dientu.store.repository.TtaQuanTriVienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TtaUserOrderService {

    private final TtaDonHangRepository donHangRepository;
    private final TtaQuanTriVienRepository nguoiDungRepository;

    /**
     * Lấy tất cả đơn hàng của người dùng hiện tại
     */
    public List<TtaDonHang> getMyOrders() {
        TtaQuanTriVien nguoiDung = getCurrentUser();
        if (nguoiDung == null) {
            return List.of();
        }
        return donHangRepository.findByTtaNguoiDung_TtaMaNguoiDung(nguoiDung.getTtaMaNguoiDung());
    }

    /**
     * Lấy chi tiết đơn hàng
     */
    public Optional<TtaDonHang> getOrderDetail(Integer orderId) {
        TtaQuanTriVien nguoiDung = getCurrentUser();
        if (nguoiDung == null) {
            return Optional.empty();
        }

        Optional<TtaDonHang> order = donHangRepository.findById(orderId);

        // Kiểm tra đơn hàng có thuộc về người dùng hiện tại không
        if (order.isPresent() &&
                order.get().getTtaNguoiDung() != null &&
                order.get().getTtaNguoiDung().getTtaMaNguoiDung().equals(nguoiDung.getTtaMaNguoiDung())) {
            return order;
        }

        return Optional.empty();
    }

    /**
     * Hủy đơn hàng (chỉ được phép hủy khi trạng thái là "Đã đặt")
     */
    @Transactional
    public boolean cancelOrder(Integer orderId) {
        Optional<TtaDonHang> orderOpt = getOrderDetail(orderId);

        if (orderOpt.isEmpty()) {
            return false;
        }

        TtaDonHang order = orderOpt.get();

        // Chỉ cho phép hủy đơn hàng có trạng thái "Đã đặt"
        if (order.getTtaTrangThai() == TtaTrangThaiDonHang.DA_DAT) {
            order.setTtaTrangThai(TtaTrangThaiDonHang.DA_HUY);
            donHangRepository.save(order);
            return true;
        }

        return false;
    }

    /**
     * Lấy thông tin người dùng hiện tại từ Security Context
     */
    private TtaQuanTriVien getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName();
        return nguoiDungRepository.findByTtaEmail(username).orElse(null);
    }

    /**
     * Kiểm tra xem đơn hàng có thể hủy không
     */
    public boolean canCancelOrder(TtaDonHang order) {
        return order != null && order.getTtaTrangThai() == TtaTrangThaiDonHang.DA_DAT;
    }
}
