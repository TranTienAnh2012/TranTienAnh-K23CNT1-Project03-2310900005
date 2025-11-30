package com.tta.dientu.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "tta_QuanTriVien")
@Data
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
}
