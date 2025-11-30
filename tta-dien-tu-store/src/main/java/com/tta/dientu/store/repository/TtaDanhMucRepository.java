package com.tta.dientu.store.repository;

import com.tta.dientu.store.entity.TtaDanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TtaDanhMucRepository extends JpaRepository<TtaDanhMuc, Integer> {

    // Tìm danh mục bằng tên
    Optional<TtaDanhMuc> findByTtaTenDanhMuc(String ttaTenDanhMuc);

    // Kiểm tra danh mục đã tồn tại chưa
    boolean existsByTtaTenDanhMuc(String ttaTenDanhMuc);

    // Tìm kiếm danh mục theo từ khóa
    @Query("SELECT d FROM TtaDanhMuc d WHERE d.ttaTenDanhMuc LIKE %:keyword% OR d.ttaMoTa LIKE %:keyword%")
    List<TtaDanhMuc> searchByKeyword(String keyword);

    // Lấy tất cả danh mục có sắp xếp
    List<TtaDanhMuc> findAllByOrderByTtaTenDanhMucAsc();
}
