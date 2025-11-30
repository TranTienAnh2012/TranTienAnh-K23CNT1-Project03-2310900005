package com.tta.dientu.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tta_SanPham")
@Data
public class TtaSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tta_MaSanPham")
    private Integer ttaMaSanPham;

    @Column(name = "tta_TenSanPham", nullable = false, length = 150)
    private String ttaTenSanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tta_MaDanhMuc", nullable = false)
    private TtaDanhMuc ttaDanhMuc;

    @Column(name = "tta_Gia", nullable = false, precision = 18, scale = 2)
    private BigDecimal ttaGia;

    @Column(name = "tta_MoTa", columnDefinition = "TEXT")
    private String ttaMoTa;

    @Column(name = "tta_HinhAnh", length = 200)
    private String ttaHinhAnh;

    @Column(name = "tta_SoLuongTon")
    private Integer ttaSoLuongTon;

    @Column(name = "tta_TrangThai")
    private Boolean ttaTrangThai;

    @Column(name = "tta_NgayThem")
    private LocalDateTime ttaNgayThem;

    @Column(name = "tta_Loai", length = 50)
    private String ttaLoai;

    @Column(name = "tta_GiaBan", precision = 18, scale = 2)
    private BigDecimal ttaGiaBan;

    @Column(name = "tta_ThuongHieu", length = 100)
    private String ttaThuongHieu;

    @Column(name = "tta_XuatXu", length = 100)
    private String ttaXuatXu;

    @Column(name = "tta_BaoHanh", length = 50)
    private String ttaBaoHanh;

    // Constructor mặc định
    public TtaSanPham() {
        this.ttaTrangThai = true;
        this.ttaNgayThem = LocalDateTime.now();
        this.ttaSoLuongTon = 0;
    }

    // Constructor với tham số cơ bản
    public TtaSanPham(String ttaTenSanPham, TtaDanhMuc ttaDanhMuc, BigDecimal ttaGia, String ttaHinhAnh) {
        this();
        this.ttaTenSanPham = ttaTenSanPham;
        this.ttaDanhMuc = ttaDanhMuc;
        this.ttaGia = ttaGia;
        this.ttaHinhAnh = ttaHinhAnh;
    }

    // Kiểm tra còn hàng
    public boolean isConHang() {
        return this.ttaSoLuongTon != null && this.ttaSoLuongTon > 0;
    }

    // Tính giá khuyến mãi (nếu có)
    public BigDecimal getGiaKhuyenMai() {
        return this.ttaGiaBan != null ? this.ttaGiaBan : this.ttaGia;
    }
}
