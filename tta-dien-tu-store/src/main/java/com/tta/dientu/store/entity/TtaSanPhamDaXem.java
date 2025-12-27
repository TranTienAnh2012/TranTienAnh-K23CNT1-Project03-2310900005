package com.tta.dientu.store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "tta_sanphamdaxem")
@Getter
@Setter
public class TtaSanPhamDaXem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tta_MaDaXem")
    private Integer ttaMaDaXem;

    @Column(name = "tta_MaNguoiDung", nullable = false)
    private Integer ttaMaNguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tta_MaSanPham", nullable = false)
    private TtaSanPham ttaSanPham;

    @Column(name = "tta_NgayXem", nullable = false)
    private LocalDateTime ttaNgayXem;

    // Constructor mặc định
    public TtaSanPhamDaXem() {
        this.ttaNgayXem = LocalDateTime.now();
    }

    // Constructor với tham số
    public TtaSanPhamDaXem(Integer ttaMaNguoiDung, Integer ttaMaSanPham) {
        this();
        this.ttaMaNguoiDung = ttaMaNguoiDung;
        // Set the product ID through the relationship
        this.ttaSanPham = new TtaSanPham();
        this.ttaSanPham.setTtaMaSanPham(ttaMaSanPham);
    }

    // Method để cập nhật thời gian xem
    public void updateViewTime() {
        this.ttaNgayXem = LocalDateTime.now();
    }

    // Helper method to get product ID
    public Integer getTtaMaSanPham() {
        return ttaSanPham != null ? ttaSanPham.getTtaMaSanPham() : null;
    }
}
