package com.tta.dientu.store.service;

import com.tta.dientu.store.entity.TtaGiaTriThuocTinh;
import com.tta.dientu.store.entity.TtaSanPham;
import com.tta.dientu.store.repository.TtaGiaTriThuocTinhRepository;
import com.tta.dientu.store.repository.TtaSanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TtaGiaTriThuocTinhService {

    private final TtaGiaTriThuocTinhRepository giaTriThuocTinhRepository;
    private final TtaSanPhamRepository sanPhamRepository;

    // Lấy tất cả thuộc tính của một sản phẩm
    public List<TtaGiaTriThuocTinh> getByProduct(Integer maSanPham) {
        return giaTriThuocTinhRepository.findByTtaSanPham_TtaMaSanPham(maSanPham);
    }

    // Lấy tất cả thuộc tính
    public List<TtaGiaTriThuocTinh> getAll() {
        return giaTriThuocTinhRepository.findAll();
    }

    // Lấy thuộc tính theo ID
    public TtaGiaTriThuocTinh getById(Integer id) {
        return giaTriThuocTinhRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thuộc tính"));
    }

    // Tạo mới thuộc tính
    @Transactional
    public TtaGiaTriThuocTinh create(Integer maSanPham, String tenThuocTinh, String giaTri) {
        TtaSanPham sanPham = sanPhamRepository.findById(maSanPham)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        TtaGiaTriThuocTinh thuocTinh = new TtaGiaTriThuocTinh();
        thuocTinh.setTtaSanPham(sanPham);
        thuocTinh.setTtaThuocTinh(tenThuocTinh);
        thuocTinh.setTtaGiaTri(giaTri);

        return giaTriThuocTinhRepository.save(thuocTinh);
    }

    // Cập nhật thuộc tính
    @Transactional
    public TtaGiaTriThuocTinh update(Integer id, String tenThuocTinh, String giaTri) {
        TtaGiaTriThuocTinh thuocTinh = getById(id);
        thuocTinh.setTtaThuocTinh(tenThuocTinh);
        thuocTinh.setTtaGiaTri(giaTri);
        return giaTriThuocTinhRepository.save(thuocTinh);
    }

    // Xóa thuộc tính
    @Transactional
    public void delete(Integer id) {
        giaTriThuocTinhRepository.deleteById(id);
    }

    // Xóa tất cả thuộc tính của một sản phẩm
    @Transactional
    public void deleteByProduct(Integer maSanPham) {
        giaTriThuocTinhRepository.deleteByTtaSanPham_TtaMaSanPham(maSanPham);
    }

    // Thêm nhiều thuộc tính cùng lúc
    @Transactional
    public List<TtaGiaTriThuocTinh> createBatch(Integer maSanPham, List<TtaGiaTriThuocTinh> thuocTinhs) {
        TtaSanPham sanPham = sanPhamRepository.findById(maSanPham)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        thuocTinhs.forEach(tt -> tt.setTtaSanPham(sanPham));
        return giaTriThuocTinhRepository.saveAll(thuocTinhs);
    }
}
