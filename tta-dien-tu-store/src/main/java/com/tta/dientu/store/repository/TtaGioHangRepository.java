package com.tta.dientu.store.repository;

import com.tta.dientu.store.entity.TtaGioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TtaGioHangRepository extends JpaRepository<TtaGioHang, Integer> {
    List<TtaGioHang> findByTtaMaNguoiDung(Integer ttaMaNguoiDung);

    Optional<TtaGioHang> findByTtaMaNguoiDungAndTtaMaSanPham(Integer ttaMaNguoiDung, Integer ttaMaSanPham);

    void deleteByTtaMaNguoiDungAndTtaMaSanPham(Integer ttaMaNguoiDung, Integer ttaMaSanPham);

    void deleteByTtaMaNguoiDung(Integer ttaMaNguoiDung);
}
