package com.tta.dientu.store.repository;

import com.tta.dientu.store.entity.TtaDanhGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TtaDanhGiaRepository extends JpaRepository<TtaDanhGia, Integer> {

    // Tìm đánh giá theo sản phẩm
    List<TtaDanhGia> findByTtaSanPham_TtaMaSanPham(Integer ttaMaSanPham);

    // Tìm đánh giá theo người dùng
    List<TtaDanhGia> findByTtaNguoiDung_TtaMaNguoiDung(Integer ttaMaNguoiDung);

    // Tìm đánh giá theo số sao
    List<TtaDanhGia> findByTtaSoSao(Integer ttaSoSao);

    // Tìm đánh giá cụ thể của người dùng cho sản phẩm
    Optional<TtaDanhGia> findByTtaSanPham_TtaMaSanPhamAndTtaNguoiDung_TtaMaNguoiDung(Integer ttaMaSanPham,
            Integer ttaMaNguoiDung);

    // Tính điểm trung bình của sản phẩm
    @Query("SELECT AVG(dg.ttaSoSao) FROM TtaDanhGia dg WHERE dg.ttaSanPham.ttaMaSanPham = :ttaMaSanPham")
    Double getDiemTrungBinhBySanPham(@Param("ttaMaSanPham") Integer ttaMaSanPham);

    // Đếm số đánh giá theo số sao
    @Query("SELECT COUNT(dg) FROM TtaDanhGia dg WHERE dg.ttaSanPham.ttaMaSanPham = :ttaMaSanPham AND dg.ttaSoSao = :ttaSoSao")
    Long countBySanPhamAndSoSao(@Param("ttaMaSanPham") Integer ttaMaSanPham, @Param("ttaSoSao") Integer ttaSoSao);

    // Lấy đánh giá có bình luận
    List<TtaDanhGia> findByTtaBinhLuanIsNotNull();

    // Phân trang đánh giá theo sản phẩm
    Page<TtaDanhGia> findByTtaSanPham_TtaMaSanPham(Integer ttaMaSanPham, Pageable pageable);

    // Kiểm tra người dùng đã đánh giá sản phẩm chưa
    boolean existsByTtaSanPham_TtaMaSanPhamAndTtaNguoiDung_TtaMaNguoiDung(Integer ttaMaSanPham, Integer ttaMaNguoiDung);

    // Đếm tổng số đánh giá theo sản phẩm
    long countByTtaSanPham_TtaMaSanPham(Integer ttaMaSanPham);
}
