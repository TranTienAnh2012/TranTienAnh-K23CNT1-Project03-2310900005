package com.tta.dientu.store.areas.admin.service;

import com.tta.dientu.store.entity.TtaDanhMuc;
import com.tta.dientu.store.repository.TtaDanhMucRepository;
import com.tta.dientu.store.repository.TtaSanPhamRepository;
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
public class TtaAdminDanhMucService {

    private final TtaDanhMucRepository ttaDanhMucRepository;
    private final TtaSanPhamRepository ttaSanPhamRepository;

    // Lấy tất cả danh mục
    public List<TtaDanhMuc> getAllDanhMuc() {
        return ttaDanhMucRepository.findAllByOrderByTtaTenDanhMucAsc();
    }

    // Lấy danh mục có phân trang
    public Page<TtaDanhMuc> getAllDanhMuc(Pageable pageable) {
        return ttaDanhMucRepository.findAll(pageable);
    }

    // Tìm kiếm danh mục
    public List<TtaDanhMuc> searchDanhMuc(String keyword) {
        return ttaDanhMucRepository.searchByKeyword(keyword);
    }

    // Lấy danh mục theo ID
    public Optional<TtaDanhMuc> getDanhMucById(Integer id) {
        return ttaDanhMucRepository.findById(id);
    }

    // Lưu danh mục
    public TtaDanhMuc saveDanhMuc(TtaDanhMuc ttaDanhMuc) {
        if (ttaDanhMuc.getTtaTenDanhMuc() == null || ttaDanhMuc.getTtaTenDanhMuc().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống");
        }
        return ttaDanhMucRepository.save(ttaDanhMuc);
    }

    // Xóa danh mục
    public boolean deleteDanhMuc(Integer id) {
        // Kiểm tra xem danh mục có sản phẩm không
        long countSanPham = ttaSanPhamRepository.countByTtaDanhMuc_TtaMaDanhMuc(id);
        if (countSanPham > 0) {
            throw new IllegalStateException("Không thể xóa danh mục vì có sản phẩm đang sử dụng");
        }

        if (ttaDanhMucRepository.existsById(id)) {
            ttaDanhMucRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Kiểm tra tên danh mục đã tồn tại chưa
    public boolean isTenDanhMucExists(String ttaTenDanhMuc, Integer excludeId) {
        Optional<TtaDanhMuc> existing = ttaDanhMucRepository.findByTtaTenDanhMuc(ttaTenDanhMuc);
        if (existing.isPresent()) {
            return !existing.get().getTtaMaDanhMuc().equals(excludeId);
        }
        return false;
    }

    // Đếm số sản phẩm theo danh mục
    public long countSanPhamByDanhMuc(Integer ttaMaDanhMuc) {
        return ttaSanPhamRepository.countByTtaDanhMuc_TtaMaDanhMuc(ttaMaDanhMuc);
    }
}
