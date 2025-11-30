package com.tta.dientu.store.areas.admin.service;

import com.tta.dientu.store.entity.TtaSanPham;
import com.tta.dientu.store.entity.TtaDanhMuc;
import com.tta.dientu.store.repository.TtaSanPhamRepository;
import com.tta.dientu.store.repository.TtaDanhMucRepository;
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
public class TtaAdminSanPhamService {

    private final TtaSanPhamRepository ttaSanPhamRepository;
    private final TtaDanhMucRepository ttaDanhMucRepository;

    // Lấy tất cả sản phẩm có phân trang
    public Page<TtaSanPham> getAllSanPham(Pageable pageable) {
        return ttaSanPhamRepository.findAll(pageable);
    }

    // Tìm kiếm sản phẩm
    public Page<TtaSanPham> searchSanPham(String keyword, Pageable pageable) {
        return ttaSanPhamRepository.searchByKeyword(keyword, pageable);
    }

    // Lấy sản phẩm theo ID
    public Optional<TtaSanPham> getSanPhamById(Integer id) {
        return ttaSanPhamRepository.findById(id);
    }

    // Lưu sản phẩm (thêm mới hoặc cập nhật)
    public TtaSanPham saveSanPham(TtaSanPham ttaSanPham) {
        // Validate dữ liệu
        if (ttaSanPham.getTtaTenSanPham() == null || ttaSanPham.getTtaTenSanPham().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }
        if (ttaSanPham.getTtaGia() == null || ttaSanPham.getTtaGia().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá sản phẩm phải lớn hơn 0");
        }
        if (ttaSanPham.getTtaDanhMuc() == null) {
            throw new IllegalArgumentException("Danh mục sản phẩm không được để trống");
        }

        return ttaSanPhamRepository.save(ttaSanPham);
    }

    // Xóa sản phẩm
    public boolean deleteSanPham(Integer id) {
        if (ttaSanPhamRepository.existsById(id)) {
            ttaSanPhamRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Cập nhật trạng thái sản phẩm
    public boolean updateTrangThai(Integer id, Boolean ttaTrangThai) {
        Optional<TtaSanPham> ttaSanPhamOpt = ttaSanPhamRepository.findById(id);
        if (ttaSanPhamOpt.isPresent()) {
            TtaSanPham ttaSanPham = ttaSanPhamOpt.get();
            ttaSanPham.setTtaTrangThai(ttaTrangThai);
            ttaSanPhamRepository.save(ttaSanPham);
            return true;
        }
        return false;
    }

    // Cập nhật số lượng tồn
    public boolean updateSoLuongTon(Integer id, Integer ttaSoLuongTon) {
        Optional<TtaSanPham> ttaSanPhamOpt = ttaSanPhamRepository.findById(id);
        if (ttaSanPhamOpt.isPresent()) {
            TtaSanPham ttaSanPham = ttaSanPhamOpt.get();
            ttaSanPham.setTtaSoLuongTon(ttaSoLuongTon);
            ttaSanPhamRepository.save(ttaSanPham);
            return true;
        }
        return false;
    }

    // Lấy tất cả danh mục
    public List<TtaDanhMuc> getAllDanhMuc() {
        return ttaDanhMucRepository.findAllByOrderByTtaTenDanhMucAsc();
    }

    // Kiểm tra tên sản phẩm đã tồn tại chưa (trừ sản phẩm hiện tại)
    public boolean isTenSanPhamExists(String ttaTenSanPham, Integer excludeId) {
        if (excludeId == null) {
            return ttaSanPhamRepository.existsByTtaTenSanPham(ttaTenSanPham);
        }
        return ttaSanPhamRepository.existsByTtaTenSanPhamAndTtaMaSanPhamNot(ttaTenSanPham, excludeId);
    }
}
