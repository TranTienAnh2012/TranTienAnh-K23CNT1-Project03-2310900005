package com.tta.dientu.store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * User-Voucher Relationship Entity
 * Replaces old TtaNguoiDungGiamGia
 */
@Entity
@Table(name = "tta_UserVoucher", uniqueConstraints = @UniqueConstraint(columnNames = { "tta_UserId", "tta_VoucherId" }))
@Getter
@Setter
public class TtaUserVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tta_Id")
    private Integer ttaId;

    // ===== Relationships =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tta_UserId", nullable = false)
    private TtaQuanTriVien ttaUser; // User đã nhận voucher (using TtaQuanTriVien with user role)

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tta_VoucherId", nullable = false)
    private TtaVoucher ttaVoucher; // Voucher được nhận

    // ===== Usage Tracking =====
    @Column(name = "tta_IsUsed")
    private Boolean ttaIsUsed = false; // Đã sử dụng chưa?

    @Column(name = "tta_UsedAt")
    private LocalDateTime ttaUsedAt; // Thời điểm sử dụng

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tta_OrderId")
    private TtaDonHang ttaOrder; // Đơn hàng đã sử dụng voucher này

    // ===== Claim Tracking =====
    @Column(name = "tta_ClaimedAt")
    private LocalDateTime ttaClaimedAt; // Thời điểm nhận voucher

    @Column(name = "tta_ExpiresAt")
    private LocalDateTime ttaExpiresAt; // Hết hạn riêng cho user (có thể khác voucher)

    // ===== Lifecycle Callbacks =====
    @PrePersist
    protected void onCreate() {
        ttaClaimedAt = LocalDateTime.now();
        if (ttaIsUsed == null) {
            ttaIsUsed = false;
        }
        // Set expiration to voucher's end date if not specified
        if (ttaExpiresAt == null && ttaVoucher != null) {
            ttaExpiresAt = ttaVoucher.getTtaEndDate();
        }
    }

    // ===== Helper Methods =====

    /**
     * Check if this user voucher is still valid
     */
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return !ttaIsUsed
                && (ttaExpiresAt == null || now.isBefore(ttaExpiresAt))
                && ttaVoucher != null
                && ttaVoucher.isValid();
    }

    /**
     * Mark voucher as used
     */
    public void markAsUsed(TtaDonHang order) {
        this.ttaIsUsed = true;
        this.ttaUsedAt = LocalDateTime.now();
        this.ttaOrder = order;

        // Increment used quantity in voucher
        if (ttaVoucher != null) {
            ttaVoucher.setTtaUsedQuantity(ttaVoucher.getTtaUsedQuantity() + 1);
        }
    }

    /**
     * Check if voucher is expired
     */
    public boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();
        return ttaExpiresAt != null && now.isAfter(ttaExpiresAt);
    }
}
