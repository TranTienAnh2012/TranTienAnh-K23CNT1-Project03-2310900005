package com.tta.dientu.store.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tta_GioHangTam")
@Data
public class TtaGioHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tta_Id")
    private Integer ttaId;

    @Column(name = "tta_MaNguoiDung")
    private Integer ttaMaNguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tta_MaSanPham", insertable = false, updatable = false)
    private TtaSanPham ttaSanPham;

    @Column(name = "tta_MaSanPham")
    private Integer ttaMaSanPham;

    @Column(name = "tta_SoLuong")
    private Integer ttaSoLuong;

    @Column(name = "tta_TrangThai")
    private String ttaTrangThai;
}
