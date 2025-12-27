package com.tta.dientu.store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tta_giatrithuoctinh")
@Getter
@Setter
public class TtaGiaTriThuocTinh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tta_GiaTriID")
    private Integer ttaGiaTriId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tta_MaSanPham", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private TtaSanPham ttaSanPham;

    @Column(name = "tta_ThuocTinhID", nullable = true)
    private Integer ttaThuocTinhId;

    @Column(name = "tta_ThuocTinh", length = 100)
    private String ttaThuocTinh;

    @Column(name = "tta_GiaTri", length = 255)
    private String ttaGiaTri;

    // Constructor mặc định
    public TtaGiaTriThuocTinh() {
    }

    // Constructor với tham số
    public TtaGiaTriThuocTinh(TtaSanPham ttaSanPham, String ttaThuocTinh, String ttaGiaTri) {
        this.ttaSanPham = ttaSanPham;
        this.ttaThuocTinh = ttaThuocTinh;
        this.ttaGiaTri = ttaGiaTri;
    }
}
