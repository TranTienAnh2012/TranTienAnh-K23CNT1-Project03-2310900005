package com.tta.dientu.store.service;

import com.tta.dientu.store.entity.TtaChiTietDonHang;
import com.tta.dientu.store.entity.TtaDonHang;
import com.tta.dientu.store.entity.TtaSanPham;
import com.tta.dientu.store.repository.TtaChiTietDonHangRepository;
import com.tta.dientu.store.repository.TtaDonHangRepository;
import com.tta.dientu.store.repository.TtaSanPhamRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TtaChiTietDonHangService {

    private final TtaChiTietDonHangRepository chiTietRepo;
    private final TtaDonHangRepository donHangRepo;
    private final TtaSanPhamRepository sanPhamRepo;

    public TtaChiTietDonHangService(TtaChiTietDonHangRepository chiTietRepo,
                                    TtaDonHangRepository donHangRepo,
                                    TtaSanPhamRepository sanPhamRepo) {
        this.chiTietRepo = chiTietRepo;
        this.donHangRepo = donHangRepo;
        this.sanPhamRepo = sanPhamRepo;
    }

    // ➤ Lấy tất cả chi tiết
    public List<TtaChiTietDonHang> getAll() {
        return chiTietRepo.findAll();
    }

    // ➤ Lấy chi tiết theo mã đơn
    public List<TtaChiTietDonHang> getByDonHang(Integer maDonHang) {
        return chiTietRepo.findByTtaDonHang_TtaMaDonHang(maDonHang);
    }

    // ➤ Tạo chi tiết đơn hàng
    public TtaChiTietDonHang create(Integer maDonHang, Integer maSanPham, Integer soLuong, String donGiaStr) {

        TtaDonHang donHang = donHangRepo.findById(maDonHang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        TtaSanPham sanPham = sanPhamRepo.findById(maSanPham)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        TtaChiTietDonHang ct = new TtaChiTietDonHang();
        ct.setTtaDonHang(donHang);
        ct.setTtaSanPham(sanPham);
        ct.setTtaSoLuong(soLuong);
        ct.setTtaDonGia(sanPham.getTtaGiaBan()); // lấy giá từ bảng sản phẩm

        return chiTietRepo.save(ct);
    }

    // ➤ Cập nhật chi tiết đơn hàng
    public TtaChiTietDonHang update(Integer id, Integer soLuong) {
        TtaChiTietDonHang ct = chiTietRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết đơn hàng"));

        ct.setTtaSoLuong(soLuong);
        return chiTietRepo.save(ct);
    }
    // Trong TtaChiTietDonHangService
    public TtaChiTietDonHang getById(Integer id) {
        return chiTietRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết đơn hàng"));
    }

    // ➤ Xóa chi tiết đơn hàng
    public void delete(Integer id) {
        chiTietRepo.deleteById(id);
    }
}
