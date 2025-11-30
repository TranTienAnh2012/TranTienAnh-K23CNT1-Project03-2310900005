package com.tta.dientu.store.repository;

import com.tta.dientu.store.entity.TtaChiTietDonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TtaChiTietDonHangRepository extends JpaRepository<TtaChiTietDonHang, Integer> {

    // Tìm chi tiết đơn hàng theo mã đơn hàng
    List<TtaChiTietDonHang> findByTtaDonHang_TtaMaDonHang(Integer ttaMaDonHang);

    // Tìm chi tiết đơn hàng theo mã sản phẩm
    List<TtaChiTietDonHang> findByTtaSanPham_TtaMaSanPham(Integer ttaMaSanPham);

    // Tính tổng số lượng sản phẩm đã bán
    @Query("SELECT SUM(ct.ttaSoLuong) FROM TtaChiTietDonHang ct WHERE ct.ttaSanPham.ttaMaSanPham = :ttaMaSanPham")
    Integer getTongSoLuongDaBan(@Param("ttaMaSanPham") Integer ttaMaSanPham);

    // Lấy top sản phẩm bán chạy
    @Query("SELECT ct.ttaSanPham.ttaMaSanPham, SUM(ct.ttaSoLuong) as totalSold " +
            "FROM TtaChiTietDonHang ct " +
            "GROUP BY ct.ttaSanPham.ttaMaSanPham " +
            "ORDER BY totalSold DESC")
    List<Object[]> findTopSanPhamBanChay();

    // Tính doanh thu theo sản phẩm
    @Query("SELECT ct.ttaSanPham.ttaMaSanPham, SUM(ct.ttaSoLuong * ct.ttaDonGia) as revenue " +
            "FROM TtaChiTietDonHang ct " +
            "GROUP BY ct.ttaSanPham.ttaMaSanPham " +
            "ORDER BY revenue DESC")
    List<Object[]> findDoanhThuTheoSanPham();

    // Kiểm tra sản phẩm có trong đơn hàng nào không
    boolean existsByTtaSanPham_TtaMaSanPham(Integer ttaMaSanPham);

    // Đếm số chi tiết đơn hàng theo đơn hàng
    long countByTtaDonHang_TtaMaDonHang(Integer ttaMaDonHang);
}
