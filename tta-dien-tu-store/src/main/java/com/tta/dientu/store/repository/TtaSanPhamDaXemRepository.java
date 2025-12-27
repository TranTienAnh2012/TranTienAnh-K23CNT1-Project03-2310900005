package com.tta.dientu.store.repository;

import com.tta.dientu.store.entity.TtaSanPhamDaXem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TtaSanPhamDaXemRepository extends JpaRepository<TtaSanPhamDaXem, Integer> {

    /**
     * Lấy danh sách sản phẩm đã xem của người dùng, sắp xếp theo thời gian xem mới
     * nhất
     */
    @Query("SELECT v FROM TtaSanPhamDaXem v WHERE v.ttaMaNguoiDung = :userId ORDER BY v.ttaNgayXem DESC")
    List<TtaSanPhamDaXem> findByTtaMaNguoiDungOrderByTtaNgayXemDesc(@Param("userId") Integer userId);

    /**
     * Tìm bản ghi xem sản phẩm cụ thể của người dùng
     */
    @Query("SELECT v FROM TtaSanPhamDaXem v WHERE v.ttaMaNguoiDung = :userId AND v.ttaSanPham.ttaMaSanPham = :productId")
    Optional<TtaSanPhamDaXem> findByTtaMaNguoiDungAndTtaMaSanPham(
            @Param("userId") Integer userId,
            @Param("productId") Integer productId);

    /**
     * Xóa tất cả lịch sử xem của người dùng
     */
    void deleteByTtaMaNguoiDung(Integer ttaMaNguoiDung);

    /**
     * Đếm số lượng sản phẩm đã xem của người dùng
     */
    @Query("SELECT COUNT(v) FROM TtaSanPhamDaXem v WHERE v.ttaMaNguoiDung = :userId")
    Long countByTtaMaNguoiDung(@Param("userId") Integer userId);
}
