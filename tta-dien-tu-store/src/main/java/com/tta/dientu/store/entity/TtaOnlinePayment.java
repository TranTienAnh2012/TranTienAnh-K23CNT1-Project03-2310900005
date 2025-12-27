package com.tta.dientu.store.entity;

import com.tta.dientu.store.enums.TtaTrangThaiThanhToanOnline;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tta_OnlinePayment")
@Getter
@Setter
public class TtaOnlinePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tta_MaThanhToan")
    private Integer ttaMaThanhToan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tta_MaDonHang", nullable = false)
    private TtaDonHang ttaDonHang;

    @Column(name = "tta_SoTien", nullable = false, precision = 18, scale = 2)
    private BigDecimal ttaSoTien;

    @Column(name = "tta_NoiDung", length = 255)
    private String ttaNoiDung;

    @Enumerated(EnumType.STRING)
    @Column(name = "tta_TrangThai", length = 20)
    private TtaTrangThaiThanhToanOnline ttaTrangThai;

    @Column(name = "tta_NgayTao")
    private LocalDateTime ttaNgayTao;

    @Column(name = "tta_NgayCapNhat")
    private LocalDateTime ttaNgayCapNhat;

    @Column(name = "tta_NgayHetHan")
    private LocalDateTime ttaNgayHetHan;

    @Column(name = "tta_TransactionReference", length = 100)
    private String ttaTransactionReference;

    @Column(name = "tta_QrCodeUrl", length = 500)
    private String ttaQrCodeUrl;

    // Constructor
    public TtaOnlinePayment() {
        this.ttaNgayTao = LocalDateTime.now();
        this.ttaNgayCapNhat = LocalDateTime.now();
        this.ttaTrangThai = TtaTrangThaiThanhToanOnline.PENDING;
        // Hết hạn sau 15 phút
        this.ttaNgayHetHan = LocalDateTime.now().plusMinutes(15);
    }

    // Helper methods
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.ttaNgayHetHan);
    }

    public boolean isPending() {
        return this.ttaTrangThai == TtaTrangThaiThanhToanOnline.PENDING;
    }

    public boolean isCompleted() {
        return this.ttaTrangThai == TtaTrangThaiThanhToanOnline.COMPLETED;
    }

    public void markAsCompleted(String transactionRef) {
        this.ttaTrangThai = TtaTrangThaiThanhToanOnline.COMPLETED;
        this.ttaTransactionReference = transactionRef;
        this.ttaNgayCapNhat = LocalDateTime.now();
    }

    public void markAsExpired() {
        this.ttaTrangThai = TtaTrangThaiThanhToanOnline.EXPIRED;
        this.ttaNgayCapNhat = LocalDateTime.now();
    }
}
