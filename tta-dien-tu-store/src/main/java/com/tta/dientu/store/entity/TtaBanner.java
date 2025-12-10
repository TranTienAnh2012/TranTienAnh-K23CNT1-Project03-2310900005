package com.tta.dientu.store.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tta_Banner")
@Data
public class TtaBanner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tta_MaBanner")
    private Integer ttaMaBanner;

    @Column(name = "tta_TenBanner", nullable = false, length = 200)
    private String ttaTenBanner;

    @Column(name = "tta_HinhAnh", length = 255)
    private String ttaHinhAnh;

    @Column(name = "tta_NoiDung", columnDefinition = "TEXT")
    private String ttaNoiDung;

    @Column(name = "tta_TrangThai")
    private Boolean ttaTrangThai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tta_MaDanhMuc")
    private TtaDanhMuc ttaDanhMuc;
}
