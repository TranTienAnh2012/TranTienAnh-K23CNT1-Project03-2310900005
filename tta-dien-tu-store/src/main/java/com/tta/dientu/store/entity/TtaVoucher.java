package com.tta.dientu.store.entity;

import com.tta.dientu.store.enums.TtaDiscountType;
import com.tta.dientu.store.enums.TtaVoucherStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Simplified Voucher Entity
 * Replaces old TtaKhuyenMai and TtaSanPhamKhuyenMai
 */
@Entity
@Table(name = "tta_Voucher")
@Getter
@Setter
public class TtaVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tta_Id")
    private Integer ttaId;

    @Column(name = "tta_Code", unique = true, nullable = false, length = 50)
    private String ttaCode; // Mã voucher: "SUMMER2024", "NEWYEAR50"

    @Column(name = "tta_Name", nullable = false, length = 200)
    private String ttaName; // Tên voucher hiển thị

    @Column(name = "tta_Description", columnDefinition = "TEXT")
    private String ttaDescription; // Mô tả chi tiết

    // ===== Discount Configuration =====
    @Enumerated(EnumType.STRING)
    @Column(name = "tta_DiscountType", nullable = false, length = 20)
    private TtaDiscountType ttaDiscountType; // PERCENT hoặc FIXED

    @Column(name = "tta_DiscountValue", nullable = false, precision = 18, scale = 2)
    private BigDecimal ttaDiscountValue; // Giá trị giảm (10 = 10% hoặc 10,000₫)

    @Column(name = "tta_MaxDiscount", precision = 18, scale = 2)
    private BigDecimal ttaMaxDiscount; // Giảm tối đa (cho PERCENT)

    @Column(name = "tta_MinOrderValue", precision = 18, scale = 2)
    private BigDecimal ttaMinOrderValue; // Giá trị đơn hàng tối thiểu

    // ===== Quantity Management =====
    @Column(name = "tta_TotalQuantity")
    private Integer ttaTotalQuantity; // Tổng số voucher có thể phát hành

    @Column(name = "tta_UsedQuantity")
    private Integer ttaUsedQuantity = 0; // Số lượng đã sử dụng

    @Column(name = "tta_LimitPerUser")
    private Integer ttaLimitPerUser = 1; // Giới hạn số lần 1 user có thể nhận

    // ===== Time Management =====
    @Column(name = "tta_StartDate")
    private LocalDateTime ttaStartDate; // Ngày bắt đầu hiệu lực

    @Column(name = "tta_EndDate")
    private LocalDateTime ttaEndDate; // Ngày hết hạn

    @Column(name = "tta_CreatedAt")
    private LocalDateTime ttaCreatedAt; // Ngày tạo voucher

    // ===== Status =====
    @Enumerated(EnumType.STRING)
    @Column(name = "tta_Status", length = 20)
    private TtaVoucherStatus ttaStatus = TtaVoucherStatus.ACTIVE; // Trạng thái

    // ===== Product/Category Application =====
    @Column(name = "tta_ApplyToAll")
    private Boolean ttaApplyToAll = true; // Áp dụng cho tất cả sản phẩm?

    @Column(name = "tta_ProductIds", columnDefinition = "TEXT")
    private String ttaProductIds; // JSON array: "[1,2,3,4]" - Danh sách ID sản phẩm

    @Column(name = "tta_CategoryIds", columnDefinition = "TEXT")
    private String ttaCategoryIds; // JSON array: "[1,2,3]" - Danh sách ID danh mục

    // ===== Relationships =====
    @OneToMany(mappedBy = "ttaVoucher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TtaUserVoucher> ttaUserVouchers; // Danh sách user đã nhận voucher

    // ===== Lifecycle Callbacks =====
    @PrePersist
    protected void onCreate() {
        ttaCreatedAt = LocalDateTime.now();
        if (ttaUsedQuantity == null) {
            ttaUsedQuantity = 0;
        }
        if (ttaLimitPerUser == null) {
            ttaLimitPerUser = 1;
        }
        if (ttaApplyToAll == null) {
            ttaApplyToAll = true;
        }
        if (ttaStatus == null) {
            ttaStatus = TtaVoucherStatus.ACTIVE;
        }
    }

    // ===== Helper Methods =====

    /**
     * Check if voucher is still valid
     */
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return ttaStatus == TtaVoucherStatus.ACTIVE
                && (ttaStartDate == null || now.isAfter(ttaStartDate))
                && (ttaEndDate == null || now.isBefore(ttaEndDate))
                && (ttaTotalQuantity == null || ttaUsedQuantity < ttaTotalQuantity);
    }

    /**
     * Check if voucher can be claimed by user
     */
    public boolean canBeClaimed() {
        return isValid() && (ttaTotalQuantity == null || ttaUsedQuantity < ttaTotalQuantity);
    }

    /**
     * Get remaining quantity
     */
    public Integer getRemainingQuantity() {
        if (ttaTotalQuantity == null) {
            return null; // Unlimited
        }
        return Math.max(0, ttaTotalQuantity - ttaUsedQuantity);
    }

    /**
     * Calculate discount amount for given order total
     */
    public BigDecimal calculateDiscount(BigDecimal orderTotal) {
        if (orderTotal == null || orderTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Check minimum order value
        if (ttaMinOrderValue != null && orderTotal.compareTo(ttaMinOrderValue) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount;
        if (ttaDiscountType == TtaDiscountType.PERCENT) {
            // Percentage discount
            discount = orderTotal.multiply(ttaDiscountValue).divide(BigDecimal.valueOf(100));

            // Apply max discount limit
            if (ttaMaxDiscount != null && discount.compareTo(ttaMaxDiscount) > 0) {
                discount = ttaMaxDiscount;
            }
        } else {
            // Fixed amount discount
            discount = ttaDiscountValue;
        }

        // Discount cannot exceed order total
        return discount.min(orderTotal);
    }
}
