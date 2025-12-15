package com.tta.dientu.store.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tta_DanhMuc")
@lombok.Getter
@lombok.Setter
public class TtaDanhMuc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tta_MaDanhMuc")
    private Integer ttaMaDanhMuc;

    @Column(name = "tta_TenDanhMuc", nullable = false, length = 100)
    private String ttaTenDanhMuc;

    @Column(name = "tta_MoTa", length = 250)
    private String ttaMoTa;

    // Constructor mặc định
    public TtaDanhMuc() {
    }

    // Constructor với tham số
    public TtaDanhMuc(String ttaTenDanhMuc, String ttaMoTa) {
        this.ttaTenDanhMuc = ttaTenDanhMuc;
        this.ttaMoTa = ttaMoTa;
    }
}
