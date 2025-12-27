package com.tta.dientu.store.areas.admin.controller;

import com.tta.dientu.store.entity.TtaChiTietDonHang;
import com.tta.dientu.store.entity.TtaDonHang;
import com.tta.dientu.store.entity.TtaSanPham;
import com.tta.dientu.store.repository.TtaDonHangRepository;
import com.tta.dientu.store.repository.TtaSanPhamRepository;
import com.tta.dientu.store.areas.admin.service.TtaChiTietDonHangService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/ttachitietdonhang")
public class TtaChiTietDonHangController {

    private final TtaChiTietDonHangService service;
    private final TtaDonHangRepository donHangRepo;
    private final TtaSanPhamRepository sanPhamRepo;

    public TtaChiTietDonHangController(TtaChiTietDonHangService service,
            TtaDonHangRepository donHangRepo,
            TtaSanPhamRepository sanPhamRepo) {
        this.service = service;
        this.donHangRepo = donHangRepo;
        this.sanPhamRepo = sanPhamRepo;
    }

    // ➤ Danh sách với phân trang
    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TtaChiTietDonHang> ttaPage = service.getAll(pageable);

        // fix null
        for (TtaChiTietDonHang item : ttaPage.getContent()) {
            if (item.getTtaDonHang() == null)
                item.setTtaDonHang(new TtaDonHang());
            if (item.getTtaSanPham() == null)
                item.setTtaSanPham(new TtaSanPham());
        }

        model.addAttribute("ttaPage", ttaPage);
        model.addAttribute("ttaList", ttaPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ttaPage.getTotalPages());
        model.addAttribute("pageTitle", "Danh sách Chi Tiết Đơn Hàng");
        return "areas/admin/ttachitietdonhang/tta-list";
    }

    // ➤ Form tạo
    @GetMapping("/create")
    public String createForm(Model model) {
        TtaChiTietDonHang ct = new TtaChiTietDonHang();
        ct.setTtaDonHang(new TtaDonHang());
        ct.setTtaSanPham(new TtaSanPham());
        model.addAttribute("ttaChiTietDonHang", ct);

        model.addAttribute("donHangs", donHangRepo.findAll());
        model.addAttribute("sanPhams", sanPhamRepo.findAll());
        model.addAttribute("pageTitle", "Thêm Chi Tiết Đơn Hàng");
        return "areas/admin/ttachitietdonhang/tta-create";
    }

    // ➤ Xử lý tạo
    @PostMapping("/create")
    public String create(@ModelAttribute("ttaChiTietDonHang") TtaChiTietDonHang ct) {
        service.create(
                ct.getTtaDonHang().getTtaMaDonHang(),
                ct.getTtaSanPham().getTtaMaSanPham(),
                ct.getTtaSoLuong(),
                null);
        return "redirect:/admin/ttachitietdonhang";
    }

    // ➤ Form edit
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        TtaChiTietDonHang ct = service.getAll().stream()
                .filter(c -> c.getTtaMaChiTiet().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết"));
        model.addAttribute("ttaChiTietDonHang", ct);
        model.addAttribute("pageTitle", "Sửa Chi Tiết Đơn Hàng");
        return "areas/admin/ttachitietdonhang/tta-edit";
    }

    // ➤ Xử lý edit
    @PostMapping("/edit/{id}")
    public String edit(@ModelAttribute("ttaChiTietDonHang") TtaChiTietDonHang ct) {
        service.update(ct.getTtaMaChiTiet(), ct.getTtaSoLuong());
        return "redirect:/admin/ttachitietdonhang";
    }

    // ➤ Xóa
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "redirect:/admin/ttachitietdonhang";
    }
}
