package com.tta.dientu.store.repository;

import com.tta.dientu.store.entity.TtaDonHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TtaDonHangRepository extends JpaRepository<TtaDonHang, Integer> {

    // Tìm đơn hàng theo người dùng
    List<TtaDonHang> findByTtaNguoiDung_TtaMaNguoiDung(Integer ttaMaNguoiDung);

    // Tìm đơn hàng theo trạng thái
    List<TtaDonHang> findByTtaTrangThai(Boolean ttaTrangThai);

    // Tìm đơn hàng theo khoảng thời gian
    List<TtaDonHang> findByTtaNgayDatHangBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Tìm đơn hàng mới nhất
    List<TtaDonHang> findTop10ByOrderByTtaNgayDatHangDesc();

    // Thống kê đơn hàng theo tháng
    @Query("SELECT COUNT(dh) FROM TtaDonHang dh WHERE YEAR(dh.ttaNgayDatHang) = :year AND MONTH(dh.ttaNgayDatHang) = :month")
    Long countByThangNam(@Param("year") int year, @Param("month") int month);

    // Tính tổng doanh thu theo tháng
    @Query("SELECT SUM(dh.ttaTongTien) FROM TtaDonHang dh WHERE YEAR(dh.ttaNgayDatHang) = :year AND MONTH(dh.ttaNgayDatHang) = :month AND dh.ttaTrangThai = true")
    BigDecimal getTongDoanhThuThang(@Param("year") int year, @Param("month") int month);

    // Phân trang đơn hàng
    Page<TtaDonHang> findAll(Pageable pageable);

    // Tìm kiếm đơn hàng theo mã hoặc tên người dùng
    @Query("SELECT dh FROM TtaDonHang dh WHERE " +
            "dh.ttaMaDonHang = :keyword OR " +
            "dh.ttaNguoiDung.ttaHoTen LIKE %:keyword% OR " +
            "dh.ttaNguoiDung.ttaEmail LIKE %:keyword%")
    Page<TtaDonHang> searchDonHang(@Param("keyword") String keyword, Pageable pageable);

    // Đếm số đơn hàng chưa xử lý
    long countByTtaTrangThaiFalse();

    List<TtaDonHang> findByTtaTrangThaiFalse();
}
