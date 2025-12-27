package com.tta.dientu.store.areas.admin.controller;

import com.tta.dientu.store.entity.TtaGiaTriThuocTinh;
import com.tta.dientu.store.entity.TtaSanPham;
import com.tta.dientu.store.repository.TtaSanPhamRepository;
import com.tta.dientu.store.service.TtaGiaTriThuocTinhService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/thuoctinh")
@RequiredArgsConstructor
public class TtaGiaTriThuocTinhController {

    private final TtaGiaTriThuocTinhService service;
    private final TtaSanPhamRepository sanPhamRepository;

    // Danh sách thuộc tính
    @GetMapping
    public String list(@RequestParam(required = false) Integer productId, Model model) {
        List<TtaGiaTriThuocTinh> thuocTinhs;

        if (productId != null) {
            thuocTinhs = service.getByProduct(productId);
            sanPhamRepository.findById(productId).ifPresent(sp -> {
                model.addAttribute("selectedProduct", sp);
            });
        } else {
            thuocTinhs = service.getAll();

            // Group attributes by product for better display
            java.util.Map<com.tta.dientu.store.entity.TtaSanPham, java.util.List<TtaGiaTriThuocTinh>> groupedAttributes = thuocTinhs
                    .stream()
                    .collect(java.util.stream.Collectors.groupingBy(TtaGiaTriThuocTinh::getTtaSanPham));
            model.addAttribute("groupedAttributes", groupedAttributes);
        }

        model.addAttribute("thuocTinhs", thuocTinhs);
        model.addAttribute("products", sanPhamRepository.findAll());
        model.addAttribute("pageTitle", "Quản lý Thuộc tính Sản phẩm");
        return "areas/admin/TtaThuocTinh/tta-list";
    }

    // Form tạo mới
    @GetMapping("/create")
    public String createForm(@RequestParam(required = false) Integer productId, Model model) {
        TtaGiaTriThuocTinh thuocTinh = new TtaGiaTriThuocTinh();

        if (productId != null) {
            sanPhamRepository.findById(productId).ifPresent(thuocTinh::setTtaSanPham);
        }

        model.addAttribute("thuocTinh", thuocTinh);
        model.addAttribute("products", sanPhamRepository.findAll());
        model.addAttribute("pageTitle", "Thêm Thuộc tính");
        return "areas/admin/TtaThuocTinh/tta-create";
    }

    // Xử lý tạo mới
    @PostMapping("/create")
    public String create(@RequestParam Integer productId,
            @RequestParam String tenThuocTinh,
            @RequestParam String giaTri,
            RedirectAttributes redirectAttributes) {
        try {
            service.create(productId, tenThuocTinh, giaTri);
            redirectAttributes.addFlashAttribute("success", "Thêm thuộc tính thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/thuoctinh?productId=" + productId;
    }

    // Form sửa
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        TtaGiaTriThuocTinh thuocTinh = service.getById(id);
        model.addAttribute("thuocTinh", thuocTinh);
        model.addAttribute("products", sanPhamRepository.findAll());
        model.addAttribute("pageTitle", "Sửa Thuộc tính");
        return "areas/admin/TtaThuocTinh/tta-edit";
    }

    // Xử lý sửa
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Integer id,
            @RequestParam String tenThuocTinh,
            @RequestParam String giaTri,
            RedirectAttributes redirectAttributes) {
        try {
            TtaGiaTriThuocTinh thuocTinh = service.update(id, tenThuocTinh, giaTri);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thuộc tính thành công!");
            return "redirect:/admin/thuoctinh?productId=" + thuocTinh.getTtaSanPham().getTtaMaSanPham();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/admin/thuoctinh";
        }
    }

    // Xóa
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            TtaGiaTriThuocTinh thuocTinh = service.getById(id);
            Integer productId = thuocTinh.getTtaSanPham().getTtaMaSanPham();
            service.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa thuộc tính thành công!");
            return "redirect:/admin/thuoctinh?productId=" + productId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/admin/thuoctinh";
        }
    }

    // Form thêm nhiều thuộc tính
    @GetMapping("/create-bulk")
    public String createBulkForm(Model model) {
        model.addAttribute("products", sanPhamRepository.findAll());
        model.addAttribute("pageTitle", "Thêm Nhiều Thuộc tính");
        return "areas/admin/TtaThuocTinh/tta-create-bulk";
    }

    // Xử lý thêm nhiều thuộc tính
    @PostMapping("/create-bulk")
    public String createBulk(@RequestParam Integer productId,
            @RequestParam("names") String[] names,
            @RequestParam("values") String[] values,
            RedirectAttributes redirectAttributes) {
        try {
            java.util.List<TtaGiaTriThuocTinh> attributes = new java.util.ArrayList<>();
            for (int i = 0; i < names.length; i++) {
                if (names[i] != null && !names[i].trim().isEmpty() &&
                        values[i] != null && !values[i].trim().isEmpty()) {
                    TtaGiaTriThuocTinh attr = new TtaGiaTriThuocTinh();
                    attr.setTtaThuocTinh(names[i].trim());
                    attr.setTtaGiaTri(values[i].trim());
                    attributes.add(attr);
                }
            }
            if (!attributes.isEmpty()) {
                service.createBatch(productId, attributes);
                redirectAttributes.addFlashAttribute("success",
                        "Đã thêm " + attributes.size() + " thuộc tính thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không có thuộc tính nào để thêm!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/thuoctinh?productId=" + productId;
    }
}
