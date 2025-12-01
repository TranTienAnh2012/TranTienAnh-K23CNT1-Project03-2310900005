package com.tta.dientu.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tta_DonHang")
@Data
public class TtaDonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tta_MaDonHang")
    private Integer ttaMaDonHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tta_MaNguoiDung", nullable = false)
    private TtaQuanTriVien ttaNguoiDung;

    @Column(name = "tta_NgayDatHang")
    private LocalDateTime ttaNgayDatHang;

    @Column(name = "tta_TongTien", nullable = false, precision = 18, scale = 2)
    private BigDecimal ttaTongTien;

    @Enumerated(EnumType.STRING)
    @Column(name = "tta_TrangThai", length = 20)
    private com.tta.dientu.store.enums.TtaTrangThaiDonHang ttaTrangThai;

    // Thông tin người nhận
    @Column(name = "tta_HoTenNguoiNhan", length = 100)
    private String ttaHoTenNguoiNhan;

    @Column(name = "tta_SoDienThoaiNguoiNhan", length = 15)
    private String ttaSoDienThoaiNguoiNhan;

    @Column(name = "tta_DiaChiNguoiNhan", length = 255)
    private String ttaDiaChiNguoiNhan;

    @Column(name = "tta_EmailNguoiNhan", length = 100)
    private String ttaEmailNguoiNhan;

    @Column(name = "tta_GhiChu", columnDefinition = "TEXT")
    private String ttaGhiChu;

    // Constructor mặc định
    public TtaDonHang() {
        this.ttaNgayDatHang = LocalDateTime.now();
        this.ttaTrangThai = com.tta.dientu.store.enums.TtaTrangThaiDonHang.DA_DAT; // Mặc định là đã đặt
        this.ttaTongTien = BigDecimal.ZERO;
    }

    // Constructor với tham số
    public TtaDonHang(TtaQuanTriVien ttaNguoiDung, BigDecimal ttaTongTien) {
        this();
        this.ttaNguoiDung = ttaNguoiDung;
        this.ttaTongTien = ttaTongTien;
    }

    // Kiểm tra trạng thái đơn hàng
    public String getTrangThaiText() {
        if (this.ttaTrangThai == null)
            return "Chưa xác định";
        return this.ttaTrangThai.getTenHienThi();
    }

    // Kiểm tra đơn hàng mới
    public boolean isDonHangMoi() {
        return this.ttaNgayDatHang != null &&
                this.ttaNgayDatHang.isAfter(LocalDateTime.now().minusDays(1));
    }

    // Kiểm tra có thể hủy không
    public boolean coTheHuy() {
        return this.ttaTrangThai != null && this.ttaTrangThai.coTheHuy();
    }
}
