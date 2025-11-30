package com.tta.dientu.store.repository;

import com.tta.dientu.store.entity.TtaSanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TtaSanPhamRepository extends JpaRepository<TtaSanPham, Integer> {

    // Tìm sản phẩm theo tên
    Optional<TtaSanPham> findByTtaTenSanPham(String ttaTenSanPham);

    // Tìm sản phẩm theo danh mục
    List<TtaSanPham> findByTtaDanhMuc_TtaMaDanhMuc(Integer ttaMaDanhMuc);

    // Tìm sản phẩm theo trạng thái
    List<TtaSanPham> findByTtaTrangThai(Boolean ttaTrangThai);

    // Tìm sản phẩm còn hàng
    @Query("SELECT sp FROM TtaSanPham sp WHERE sp.ttaSoLuongTon > 0 AND sp.ttaTrangThai = true")
    List<TtaSanPham> findSanPhamConHang();

    // Tìm sản phẩm theo khoảng giá
    @Query("SELECT sp FROM TtaSanPham sp WHERE sp.ttaGia BETWEEN :minPrice AND :maxPrice AND sp.ttaTrangThai = true")
    List<TtaSanPham> findByGiaBetween(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    // Tìm sản phẩm theo danh mục và khoảng giá
    @Query("SELECT sp FROM TtaSanPham sp WHERE sp.ttaDanhMuc.ttaMaDanhMuc = :categoryId AND sp.ttaGia BETWEEN :minPrice AND :maxPrice AND sp.ttaTrangThai = true")
    List<TtaSanPham> findByCategoryAndPrice(@Param("categoryId") Integer categoryId,
            @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    // Tìm kiếm sản phẩm theo từ khóa
    @Query("SELECT sp FROM TtaSanPham sp WHERE " +
            "(sp.ttaTenSanPham LIKE %:keyword% OR sp.ttaMoTa LIKE %:keyword% OR sp.ttaThuongHieu LIKE %:keyword%) " +
            "AND sp.ttaTrangThai = true")
    Page<TtaSanPham> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Lấy sản phẩm mới nhất
    List<TtaSanPham> findTop10ByTtaTrangThaiTrueOrderByTtaNgayThemDesc();

    // Lấy sản phẩm bán chạy (có thể cần thêm logic order by số lượng bán)
    @Query("SELECT sp FROM TtaSanPham sp WHERE sp.ttaTrangThai = true ORDER BY sp.ttaSoLuongTon DESC")
    List<TtaSanPham> findTop10ByTtaTrangThaiTrueOrderByTtaSoLuongTonDesc();

    // Kiểm tra tên sản phẩm đã tồn tại chưa
    boolean existsByTtaTenSanPham(String ttaTenSanPham);

    // Đếm số sản phẩm theo danh mục
    long countByTtaDanhMuc_TtaMaDanhMuc(Integer ttaMaDanhMuc);

    // Kiểm tra tên sản phẩm tồn tại ngoại trừ ID cụ thể
    boolean existsByTtaTenSanPhamAndTtaMaSanPhamNot(String ttaTenSanPham, Integer ttaMaSanPham);
}
