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
}
