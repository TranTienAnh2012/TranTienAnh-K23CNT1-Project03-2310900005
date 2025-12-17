package com.tta.dientu.store.service;

import com.tta.dientu.store.dto.TtaCartItem;
import com.tta.dientu.store.entity.TtaGioHang;
import com.tta.dientu.store.entity.TtaQuanTriVien;
import com.tta.dientu.store.repository.TtaGioHangRepository;
import com.tta.dientu.store.repository.TtaQuanTriVienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TtaCartService {
    private final TtaGioHangRepository ttaGioHangRepository;
    private final TtaQuanTriVienRepository ttaQuanTriVienRepository;
    private final com.tta.dientu.store.repository.TtaDonHangRepository ttaDonHangRepository;
    private final com.tta.dientu.store.repository.TtaChiTietDonHangRepository ttaChiTietDonHangRepository;
    private final com.tta.dientu.store.repository.TtaSanPhamRepository ttaSanPhamRepository;

    private final TtaUserVoucherService ttaUserVoucherService;

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        String ttaEmail = authentication.getName();
        return ttaQuanTriVienRepository.findByTtaEmail(ttaEmail)
                .map(TtaQuanTriVien::getTtaMaNguoiDung)
                .orElse(null);
    }

    @Transactional
    public void checkout(String hoTen, String soDienThoai, String diaChi, String email, String ghiChu,
            String voucherCode) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("Bạn cần đăng nhập để thanh toán");
        }

        List<TtaCartItem> cartItems = getCartItems();
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }

        TtaQuanTriVien user = ttaQuanTriVienRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        java.math.BigDecimal total = java.math.BigDecimal.valueOf(getTotal());
        java.math.BigDecimal discount = java.math.BigDecimal.ZERO;

        // Apply voucher if provided
        if (voucherCode != null && !voucherCode.trim().isEmpty()) {
            discount = ttaUserVoucherService.calculateDiscount(userId, voucherCode, total);
        }

        // Tạo đơn hàng
        com.tta.dientu.store.entity.TtaDonHang donHang = new com.tta.dientu.store.entity.TtaDonHang();
        donHang.setTtaNguoiDung(user);
        donHang.setTtaNgayDatHang(java.time.LocalDateTime.now());
        donHang.setTtaTongTien(total.subtract(discount).max(java.math.BigDecimal.ZERO));
        donHang.setTtaTrangThai(com.tta.dientu.store.enums.TtaTrangThaiDonHang.DA_DAT);

        donHang.setTtaHoTenNguoiNhan(hoTen);
        donHang.setTtaSoDienThoaiNguoiNhan(soDienThoai);
        donHang.setTtaDiaChiNguoiNhan(diaChi);
        donHang.setTtaEmailNguoiNhan(email);
        donHang.setTtaGhiChu(ghiChu);

        donHang = ttaDonHangRepository.save(donHang);

        // Mark voucher as used
        if (voucherCode != null && !voucherCode.trim().isEmpty()) {
            ttaUserVoucherService.useVoucher(userId, voucherCode, donHang);
        }

        // Tạo chi tiết đơn hàng
        for (TtaCartItem item : cartItems) {
            com.tta.dientu.store.entity.TtaChiTietDonHang chiTiet = new com.tta.dientu.store.entity.TtaChiTietDonHang();
            chiTiet.setTtaDonHang(donHang);
            chiTiet.setTtaSanPham(item.getTtaSanPham());
            chiTiet.setTtaSoLuong(item.getTtaSoLuong());
            chiTiet.setTtaDonGia(item.getTtaSanPham().getGiaKhuyenMai());

            ttaChiTietDonHangRepository.save(chiTiet);
        }

        // Xóa giỏ hàng
        clearCart();
    }

    public java.math.BigDecimal calculateVoucherDiscount(Integer userId, String voucherCode,
            java.math.BigDecimal total) {
        return ttaUserVoucherService.calculateDiscount(userId, voucherCode, total);
    }

    public List<TtaCartItem> getCartItems() {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return new ArrayList<>();
        }
        List<TtaGioHang> ttaGioHangs = ttaGioHangRepository.findByTtaMaNguoiDung(userId);
        return ttaGioHangs.stream()
                .map(gh -> new TtaCartItem(gh.getTtaSanPham(), gh.getTtaSoLuong()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addToCart(Integer ttaSanPhamId, int ttaSoLuong) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("Bạn cần đăng nhập để thêm vào giỏ hàng");
        }

        Optional<TtaGioHang> existingItem = ttaGioHangRepository.findByTtaMaNguoiDungAndTtaMaSanPham(userId,
                ttaSanPhamId);

        if (existingItem.isPresent()) {
            TtaGioHang ttaGioHang = existingItem.get();
            ttaGioHang.setTtaSoLuong(ttaGioHang.getTtaSoLuong() + ttaSoLuong);
            ttaGioHangRepository.save(ttaGioHang);
        } else {
            TtaGioHang ttaGioHang = new TtaGioHang();
            ttaGioHang.setTtaMaNguoiDung(userId);
            ttaGioHang.setTtaMaSanPham(ttaSanPhamId);
            ttaGioHang.setTtaSoLuong(ttaSoLuong);
            ttaGioHang.setTtaTrangThai("active");
            ttaGioHangRepository.save(ttaGioHang);
        }
    }

    @Transactional
    public void removeFromCart(Integer ttaSanPhamId) {
        Integer userId = getCurrentUserId();
        if (userId != null) {
            ttaGioHangRepository.deleteByTtaMaNguoiDungAndTtaMaSanPham(userId, ttaSanPhamId);
        }
    }

    @Transactional
    public void updateQuantity(Integer ttaSanPhamId, int ttaSoLuong) {
        Integer userId = getCurrentUserId();
        if (userId != null) {
            Optional<TtaGioHang> existingItem = ttaGioHangRepository.findByTtaMaNguoiDungAndTtaMaSanPham(userId,
                    ttaSanPhamId);
            if (existingItem.isPresent()) {
                TtaGioHang ttaGioHang = existingItem.get();
                ttaGioHang.setTtaSoLuong(ttaSoLuong);
                ttaGioHangRepository.save(ttaGioHang);
            }
        }
    }

    @Transactional
    public void clearCart() {
        Integer userId = getCurrentUserId();
        if (userId != null) {
            ttaGioHangRepository.deleteByTtaMaNguoiDung(userId);
        }
    }

    public double getTotal() {
        return getCartItems().stream()
                .mapToDouble(TtaCartItem::getThanhTien)
                .sum();
    }

    public int getCount() {
        return getCartItems().stream().mapToInt(TtaCartItem::getTtaSoLuong).sum();
    }

    // Create order directly from Buy Now (skip cart)
    @Transactional
    public Integer createDirectOrder(Integer productId, int quantity, String hoTen, String soDienThoai, String diaChi,
            String ghiChu, String voucherCode) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("Bạn cần đăng nhập để mua hàng");
        }

        // Load product
        com.tta.dientu.store.entity.TtaSanPham product = ttaSanPhamRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        // Check stock
        if (product.getTtaSoLuongTon() < quantity) {
            throw new RuntimeException("Sản phẩm không đủ số lượng");
        }

        TtaQuanTriVien user = ttaQuanTriVienRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Calculate total
        java.math.BigDecimal total = product.getTtaGia().multiply(java.math.BigDecimal.valueOf(quantity));
        java.math.BigDecimal discount = java.math.BigDecimal.ZERO;

        // Apply voucher
        if (voucherCode != null && !voucherCode.trim().isEmpty()) {
            discount = ttaUserVoucherService.calculateDiscount(userId, voucherCode, total);
        }

        // Create order
        com.tta.dientu.store.entity.TtaDonHang donHang = new com.tta.dientu.store.entity.TtaDonHang();
        donHang.setTtaNguoiDung(user);
        donHang.setTtaNgayDatHang(java.time.LocalDateTime.now());
        donHang.setTtaTongTien(total.subtract(discount).max(java.math.BigDecimal.ZERO));
        donHang.setTtaTrangThai(com.tta.dientu.store.enums.TtaTrangThaiDonHang.DA_DAT);
        donHang.setTtaHoTenNguoiNhan(hoTen);
        donHang.setTtaSoDienThoaiNguoiNhan(soDienThoai);
        donHang.setTtaDiaChiNguoiNhan(diaChi);
        donHang.setTtaGhiChu(ghiChu);

        donHang = ttaDonHangRepository.save(donHang);

        // Mark voucher as used
        if (voucherCode != null && !voucherCode.trim().isEmpty()) {
            ttaUserVoucherService.useVoucher(userId, voucherCode, donHang);
        }

        // Create order detail
        com.tta.dientu.store.entity.TtaChiTietDonHang chiTiet = new com.tta.dientu.store.entity.TtaChiTietDonHang();
        chiTiet.setTtaDonHang(donHang);
        chiTiet.setTtaSanPham(product);
        chiTiet.setTtaSoLuong(quantity);
        chiTiet.setTtaDonGia(product.getTtaGia());

        ttaChiTietDonHangRepository.save(chiTiet);

        // Update stock
        product.setTtaSoLuongTon(product.getTtaSoLuongTon() - quantity);
        ttaSanPhamRepository.save(product);

        return donHang.getTtaMaDonHang();
    }
}
