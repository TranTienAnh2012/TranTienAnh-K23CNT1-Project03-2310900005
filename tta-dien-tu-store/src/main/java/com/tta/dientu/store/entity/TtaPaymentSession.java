package com.tta.dientu.store.entity;

import com.tta.dientu.store.enums.TtaTrangThaiSession;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tta_PaymentSession")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TtaPaymentSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tta_MaSession")
    private Integer ttaMaSession;

    // Thông tin khách hàng
    @Column(name = "tta_HoTen", nullable = false, length = 100)
    private String ttaHoTen;

    @Column(name = "tta_SoDienThoai", nullable = false, length = 15)
    private String ttaSoDienThoai;

    @Column(name = "tta_DiaChi", nullable = false, length = 255)
    private String ttaDiaChi;

    @Column(name = "tta_Email", length = 100)
    private String ttaEmail;

    @Column(name = "tta_GhiChu", columnDefinition = "TEXT")
    private String ttaGhiChu;

    // Thông tin giỏ hàng (JSON)
    @Column(name = "tta_CartDataJson", columnDefinition = "TEXT")
    private String ttaCartDataJson;

    // Thông tin thanh toán
    @Column(name = "tta_TongTien", nullable = false, precision = 18, scale = 2)
    private BigDecimal ttaTongTien;

    @Column(name = "tta_VoucherCode", length = 50)
    private String ttaVoucherCode;

    @Column(name = "tta_GiamGia", precision = 18, scale = 2)
    private BigDecimal ttaGiamGia;

    @Column(name = "tta_NoiDungCK", length = 100)
    private String ttaNoiDungCK; // TTA + sessionId

    // QR Code
    @Column(name = "tta_QrCodeUrl", length = 500)
    private String ttaQrCodeUrl;

    // Trạng thái
    @Enumerated(EnumType.STRING)
    @Column(name = "tta_TrangThai", nullable = false, length = 20)
    private TtaTrangThaiSession ttaTrangThai;

    // Timestamps
    @Column(name = "tta_NgayTao")
    private LocalDateTime ttaNgayTao;

    @Column(name = "tta_NgayCapNhat")
    private LocalDateTime ttaNgayCapNhat;

    @Column(name = "tta_NgayHetHan")
    private LocalDateTime ttaNgayHetHan; // 15 phút từ khi tạo

    // Reference to created order (sau khi thanh toán thành công)
    @OneToOne
    @JoinColumn(name = "tta_MaDonHang")
    private TtaDonHang ttaDonHang;

    // Transaction reference from bank
    @Column(name = "tta_TransactionReference", length = 100)
    private String ttaTransactionReference;

    @PrePersist
    protected void onCreate() {
        ttaNgayTao = LocalDateTime.now();
        ttaNgayCapNhat = LocalDateTime.now();
        if (ttaTrangThai == null) {
            ttaTrangThai = TtaTrangThaiSession.PENDING;
        }
        // Set expiry time to 15 minutes from now
        if (ttaNgayHetHan == null) {
            ttaNgayHetHan = LocalDateTime.now().plusMinutes(15);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        ttaNgayCapNhat = LocalDateTime.now();
    }

    // Helper methods
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(ttaNgayHetHan);
    }

    public boolean isPending() {
        return ttaTrangThai == TtaTrangThaiSession.PENDING;
    }

    public boolean isPaid() {
        return ttaTrangThai == TtaTrangThaiSession.PAID;
    }

    public boolean isCancelled() {
        return ttaTrangThai == TtaTrangThaiSession.CANCELLED;
    }

    public void markAsPaid(String transactionRef) {
        this.ttaTrangThai = TtaTrangThaiSession.PAID;
        this.ttaTransactionReference = transactionRef;
        this.ttaNgayCapNhat = LocalDateTime.now();
    }

    public void markAsCancelled() {
        this.ttaTrangThai = TtaTrangThaiSession.CANCELLED;
        this.ttaNgayCapNhat = LocalDateTime.now();
    }

    public void markAsExpired() {
        this.ttaTrangThai = TtaTrangThaiSession.EXPIRED;
        this.ttaNgayCapNhat = LocalDateTime.now();
    }
}
