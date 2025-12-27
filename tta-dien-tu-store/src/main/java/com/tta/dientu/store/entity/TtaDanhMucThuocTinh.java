package com.tta.dientu.store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tta_danhmucthuoctinh")
@Getter
@Setter
public class TtaDanhMucThuocTinh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tta_Id")
    private Integer ttaId;

    @Column(name = "tta_MaDanhMuc")
    private Integer ttaMaDanhMuc;

    @Column(name = "tta_ThuocTinhID")
    private Integer ttaThuocTinhId;

    @Column(name = "tta_TenDanhMuc", length = 100)
    private String ttaTenDanhMuc;

    @Column(name = "tta_MoTa", columnDefinition = "TEXT")
    private String ttaMoTa;

    @Column(name = "tta_ThuTu")
    private Integer ttaThuTu;

    // Constructor mặc định
    public TtaDanhMucThuocTinh() {
        this.ttaThuTu = 0;
    }
}
