package com.tta.dientu.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "tta_DanhGia")
@Data
public class TtaDanhGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tta_MaDanhGia")
    private Integer ttaMaDanhGia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tta_MaSanPham", nullable = false)
    private TtaSanPham ttaSanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tta_MaNguoiDung", nullable = false)
    private TtaQuanTriVien ttaNguoiDung;

    @Column(name = "tta_SoSao")
    private Integer ttaSoSao;

    @Column(name = "tta_BinhLuan", length = 500)
    private String ttaBinhLuan;

    @Column(name = "tta_NgayDanhGia")
    private LocalDateTime ttaNgayDanhGia;

    // Constructor mặc định
    public TtaDanhGia() {
        this.ttaNgayDanhGia = LocalDateTime.now();
    }

    // Constructor với tham số
    public TtaDanhGia(TtaSanPham ttaSanPham, TtaQuanTriVien ttaNguoiDung, Integer ttaSoSao, String ttaBinhLuan) {
        this();
        this.ttaSanPham = ttaSanPham;
        this.ttaNguoiDung = ttaNguoiDung;
        this.ttaSoSao = ttaSoSao;
        this.ttaBinhLuan = ttaBinhLuan;
    }

    // Kiểm tra số sao hợp lệ
    public boolean isSoSaoHopLe() {
        return this.ttaSoSao != null && this.ttaSoSao >= 1 && this.ttaSoSao <= 5;
    }

    // Kiểm tra đánh giá có bình luận không
    public boolean hasBinhLuan() {
        return this.ttaBinhLuan != null && !this.ttaBinhLuan.trim().isEmpty();
    }
}
