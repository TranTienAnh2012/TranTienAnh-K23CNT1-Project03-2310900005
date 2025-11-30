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

    @Column(name = "tta_TrangThai")
    private Boolean ttaTrangThai;

    // Constructor mặc định
    public TtaDonHang() {
        this.ttaNgayDatHang = LocalDateTime.now();
        this.ttaTrangThai = false; // Mặc định là chưa xử lý
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
        return this.ttaTrangThai ? "Đã hoàn thành" : "Đang xử lý";
    }

    // Kiểm tra đơn hàng mới
    public boolean isDonHangMoi() {
        return this.ttaNgayDatHang != null &&
                this.ttaNgayDatHang.isAfter(LocalDateTime.now().minusDays(1));
    }
}
