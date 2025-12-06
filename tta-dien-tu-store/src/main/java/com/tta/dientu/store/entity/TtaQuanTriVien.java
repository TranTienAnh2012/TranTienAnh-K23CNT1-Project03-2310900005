package com.tta.dientu.store.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tta_QuanTriVien")
public class TtaQuanTriVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tta_MaNguoiDung")
    private Integer ttaMaNguoiDung;

    @Column(name = "tta_HoTen", nullable = false, length = 100)
    private String ttaHoTen;

    @Column(name = "tta_Email", nullable = false, length = 100, unique = true)
    private String ttaEmail;

    @Column(name = "tta_MatKhau", nullable = false, length = 100)
    private String ttaMatKhau;

    @Column(name = "tta_VaiTro")
    private Integer ttaVaiTro;

    @Column(name = "tta_NgayDangKy")
    private LocalDateTime ttaNgayDangKy;

    // Constructor mặc định
    public TtaQuanTriVien() {
        this.ttaNgayDangKy = LocalDateTime.now();
        this.ttaVaiTro = 0; // Mặc định là user thường
    }

    // Getters and Setters
    public Integer getTtaMaNguoiDung() {
        return ttaMaNguoiDung;
    }

    public void setTtaMaNguoiDung(Integer ttaMaNguoiDung) {
        this.ttaMaNguoiDung = ttaMaNguoiDung;
    }

    public String getTtaHoTen() {
        return ttaHoTen;
    }

    public void setTtaHoTen(String ttaHoTen) {
        this.ttaHoTen = ttaHoTen;
    }

    public String getTtaEmail() {
        return ttaEmail;
    }

    public void setTtaEmail(String ttaEmail) {
        this.ttaEmail = ttaEmail;
    }

    public String getTtaMatKhau() {
        return ttaMatKhau;
    }

    public void setTtaMatKhau(String ttaMatKhau) {
        this.ttaMatKhau = ttaMatKhau;
    }

    public Integer getTtaVaiTro() {
        return ttaVaiTro;
    }

    public void setTtaVaiTro(Integer ttaVaiTro) {
        this.ttaVaiTro = ttaVaiTro;
    }

    public LocalDateTime getTtaNgayDangKy() {
        return ttaNgayDangKy;
    }

    public void setTtaNgayDangKy(LocalDateTime ttaNgayDangKy) {
        this.ttaNgayDangKy = ttaNgayDangKy;
    }
}
