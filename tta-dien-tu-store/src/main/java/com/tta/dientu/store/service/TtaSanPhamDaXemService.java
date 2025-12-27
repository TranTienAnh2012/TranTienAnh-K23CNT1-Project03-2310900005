package com.tta.dientu.store.service;

import com.tta.dientu.store.entity.TtaSanPham;
import com.tta.dientu.store.entity.TtaSanPhamDaXem;
import com.tta.dientu.store.repository.TtaSanPhamDaXemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TtaSanPhamDaXemService {

    private final TtaSanPhamDaXemRepository ttaSanPhamDaXemRepository;

    /**
     * Lưu hoặc cập nhật lịch sử xem sản phẩm
     * Nếu sản phẩm đã được xem trước đó, cập nhật thời gian xem
     * Nếu chưa, tạo bản ghi mới
     */
    @Transactional
    public void trackProductView(Integer userId, Integer productId) {
        if (userId == null || productId == null) {
            return;
        }

        Optional<TtaSanPhamDaXem> existingView = ttaSanPhamDaXemRepository
                .findByTtaMaNguoiDungAndTtaMaSanPham(userId, productId);

        if (existingView.isPresent()) {
            // Cập nhật thời gian xem
            TtaSanPhamDaXem view = existingView.get();
            view.updateViewTime();
            ttaSanPhamDaXemRepository.save(view);
        } else {
            // Tạo bản ghi mới
            TtaSanPhamDaXem newView = new TtaSanPhamDaXem(userId, productId);
            ttaSanPhamDaXemRepository.save(newView);
        }
    }

    /**
     * Lấy danh sách sản phẩm đã xem gần đây của người dùng
     * 
     * @param userId ID người dùng
     * @param limit  Số lượng sản phẩm tối đa cần lấy
     * @return Danh sách sản phẩm đã xem
     */
    public List<TtaSanPham> getRecentlyViewedProducts(Integer userId, int limit) {
        if (userId == null) {
            return List.of();
        }

        List<TtaSanPhamDaXem> viewedProducts = ttaSanPhamDaXemRepository
                .findByTtaMaNguoiDungOrderByTtaNgayXemDesc(userId);

        return viewedProducts.stream()
                .limit(limit)
                .map(TtaSanPhamDaXem::getTtaSanPham)
                .filter(product -> product != null && product.getTtaTrangThai()) // Chỉ lấy sản phẩm còn hoạt động
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách sản phẩm đã xem, loại trừ sản phẩm hiện tại
     * 
     * @param userId           ID người dùng
     * @param currentProductId ID sản phẩm hiện tại cần loại trừ
     * @param limit            Số lượng sản phẩm tối đa
     * @return Danh sách sản phẩm đã xem (không bao gồm sản phẩm hiện tại)
     */
    public List<TtaSanPham> getRecentlyViewedProductsExcluding(Integer userId, Integer currentProductId, int limit) {
        if (userId == null) {
            return List.of();
        }

        List<TtaSanPhamDaXem> viewedProducts = ttaSanPhamDaXemRepository
                .findByTtaMaNguoiDungOrderByTtaNgayXemDesc(userId);

        return viewedProducts.stream()
                .filter(view -> !view.getTtaMaSanPham().equals(currentProductId))
                .limit(limit)
                .map(TtaSanPhamDaXem::getTtaSanPham)
                .filter(product -> product != null && product.getTtaTrangThai())
                .collect(Collectors.toList());
    }

    /**
     * Xóa toàn bộ lịch sử xem của người dùng
     */
    @Transactional
    public void clearViewingHistory(Integer userId) {
        if (userId != null) {
            ttaSanPhamDaXemRepository.deleteByTtaMaNguoiDung(userId);
        }
    }

    /**
     * Đếm số lượng sản phẩm đã xem của người dùng
     */
    public Long getViewedProductCount(Integer userId) {
        if (userId == null) {
            return 0L;
        }
        return ttaSanPhamDaXemRepository.countByTtaMaNguoiDung(userId);
    }
}
