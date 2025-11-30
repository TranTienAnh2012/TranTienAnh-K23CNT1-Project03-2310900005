package com.tta.dientu.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "tta_ChiTietDonHang")
@Data
public class TtaChiTietDonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tta_MaChiTiet")
    private Integer ttaMaChiTiet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tta_MaDonHang", nullable = false)
    private TtaDonHang ttaDonHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tta_MaSanPham", nullable = false)
    private TtaSanPham ttaSanPham;

    @Column(name = "tta_SoLuong", nullable = false)
    private Integer ttaSoLuong;

    @Column(name = "tta_DonGia", nullable = false, precision = 18, scale = 2)
    private BigDecimal ttaDonGia;

    // Constructor mặc định
    public TtaChiTietDonHang() {
    }

    // Constructor với tham số
    public TtaChiTietDonHang(TtaDonHang ttaDonHang, TtaSanPham ttaSanPham, Integer ttaSoLuong, BigDecimal ttaDonGia) {
        this.ttaDonHang = ttaDonHang;
        this.ttaSanPham = ttaSanPham;
        this.ttaSoLuong = ttaSoLuong;
        this.ttaDonGia = ttaDonGia;
    }

    // Tính thành tiền cho chi tiết đơn hàng
    public BigDecimal getThanhTien() {
        if (this.ttaSoLuong != null && this.ttaDonGia != null) {
            return this.ttaDonGia.multiply(BigDecimal.valueOf(this.ttaSoLuong));
        }
        return BigDecimal.ZERO;
    }

    // Kiểm tra số lượng hợp lệ
    public boolean isSoLuongHopLe() {
        return this.ttaSoLuong != null && this.ttaSoLuong > 0 &&
                this.ttaSanPham != null && this.ttaSanPham.getTtaSoLuongTon() >= this.ttaSoLuong;
    }
}