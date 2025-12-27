package com.tta.dientu.store.repository;

import com.tta.dientu.store.entity.TtaDanhMucThuocTinh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TtaDanhMucThuocTinhRepository extends JpaRepository<TtaDanhMucThuocTinh, Integer> {

    // Lấy tất cả danh mục sắp xếp theo thứ tự
    List<TtaDanhMucThuocTinh> findAllByOrderByTtaThuTuAsc();

    // Lấy tất cả danh mục sắp xếp theo tên
    List<TtaDanhMucThuocTinh> findAllByOrderByTtaTenDanhMucAsc();
}
