package com.tta.dientu.store.areas.admin.service;

import com.tta.dientu.store.entity.TtaDonHang;
import com.tta.dientu.store.entity.TtaChiTietDonHang;
import com.tta.dientu.store.enums.TtaTrangThaiDonHang;
import com.tta.dientu.store.repository.TtaDonHangRepository;
import com.tta.dientu.store.repository.TtaChiTietDonHangRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TtaAdminDonHangService {

    private final TtaDonHangRepository ttaDonHangRepository;
    private final TtaChiTietDonHangRepository ttaChiTietDonHangRepository;

    // Lấy tất cả đơn hàng có phân trang
    public Page<TtaDonHang> getAllDonHang(Pageable pageable) {
        return ttaDonHangRepository.findAll(pageable);
    }

    // Tìm kiếm đơn hàng
    public Page<TtaDonHang> searchDonHang(String keyword, Pageable pageable) {
        return ttaDonHangRepository.searchDonHang(keyword, pageable);
    }

    // Lấy đơn hàng theo ID
    public Optional<TtaDonHang> getDonHangById(Integer id) {
        return ttaDonHangRepository.findById(id);
    }

    // Lấy chi tiết đơn hàng
    public List<TtaChiTietDonHang> getChiTietDonHang(Integer ttaMaDonHang) {
        return ttaChiTietDonHangRepository.findByTtaDonHang_TtaMaDonHang(ttaMaDonHang);
    }

    // Cập nhật trạng thái đơn hàng
    public boolean updateTrangThaiDonHang(Integer id, TtaTrangThaiDonHang ttaTrangThai) {
        Optional<TtaDonHang> ttaDonHangOpt = ttaDonHangRepository.findById(id);
        if (ttaDonHangOpt.isPresent()) {
            TtaDonHang ttaDonHang = ttaDonHangOpt.get();
            ttaDonHang.setTtaTrangThai(ttaTrangThai);
            ttaDonHangRepository.save(ttaDonHang);
            return true;
        }
        return false;
    }

    // Lấy đơn hàng chưa xử lý (Đã đặt và Đang xử lý)
    public List<TtaDonHang> getDonHangChuaXuLy() {
        return ttaDonHangRepository.findByTtaTrangThaiIn(
                List.of(TtaTrangThaiDonHang.DA_DAT, TtaTrangThaiDonHang.DANG_XU_LY));
    }

    // Lấy đơn hàng mới nhất
    public List<TtaDonHang> getDonHangMoiNhat() {
        return ttaDonHangRepository.findTop10ByOrderByTtaNgayDatHangDesc();
    }

    // Thống kê đơn hàng
    public Object[] getThongKeDonHang() {
        long tongDonHang = ttaDonHangRepository.count();
        long donHangDaDat = ttaDonHangRepository.countByTtaTrangThai(TtaTrangThaiDonHang.DA_DAT);
        long donHangDangXuLy = ttaDonHangRepository.countByTtaTrangThai(TtaTrangThaiDonHang.DANG_XU_LY);
        long donHangDangGiao = ttaDonHangRepository.countByTtaTrangThai(TtaTrangThaiDonHang.DANG_GIAO);
        long donHangDaGiao = ttaDonHangRepository.countByTtaTrangThai(TtaTrangThaiDonHang.DA_GIAO);
        long donHangDaHuy = ttaDonHangRepository.countByTtaTrangThai(TtaTrangThaiDonHang.DA_HUY);

        return new Object[] { tongDonHang, donHangDaDat, donHangDangXuLy, donHangDangGiao, donHangDaGiao,
                donHangDaHuy };
    }

    // Xóa đơn hàng (chỉ xóa khi có thể hủy)
    public boolean deleteDonHang(Integer id) {
        Optional<TtaDonHang> ttaDonHangOpt = ttaDonHangRepository.findById(id);
        if (ttaDonHangOpt.isPresent() && ttaDonHangOpt.get().coTheHuy()) {
            // Xóa chi tiết đơn hàng trước
            List<TtaChiTietDonHang> ttaChiTietList = ttaChiTietDonHangRepository.findByTtaDonHang_TtaMaDonHang(id);
            ttaChiTietDonHangRepository.deleteAll(ttaChiTietList);

            // Xóa đơn hàng
            ttaDonHangRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
