package com.tta.dientu.store.repository;

import com.tta.dientu.store.entity.TtaGiaTriThuocTinh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TtaGiaTriThuocTinhRepository extends JpaRepository<TtaGiaTriThuocTinh, Integer> {

    // Lấy tất cả thuộc tính của một sản phẩm
    List<TtaGiaTriThuocTinh> findByTtaSanPham_TtaMaSanPham(Integer maSanPham);

    // Xóa tất cả thuộc tính của một sản phẩm
    void deleteByTtaSanPham_TtaMaSanPham(Integer maSanPham);

    // Tìm theo tên thuộc tính
    List<TtaGiaTriThuocTinh> findByTtaThuocTinhContaining(String tenThuocTinh);
}
