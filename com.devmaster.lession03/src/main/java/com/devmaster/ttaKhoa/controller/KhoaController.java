package com.devmaster.ttaKhoa.controller;

import com.devmaster.ttaKhoa.entity.Khoa;
import com.devmaster.ttaKhoa.service.ServiceKhoa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class: KhoaController
 * <p>Thực hiện các API REST cho đối tượng Khoa</p>
 * @Name Trần Tiến Anh
 * @Date 10/11/2025
 * @version 1.1
 */

@RestController
@RequestMapping("/khoa") // endpoint chính: /khoa/...
public class KhoaController {

    @Autowired
    private ServiceKhoa serviceKhoa;

    // Lấy toàn bộ danh sách khoa
    @GetMapping("/list")
    public List<Khoa> getAllKhoas() {
        return serviceKhoa.getAllKhoas();
    }

    // Lấy thông tin 1 khoa theo ID
    @GetMapping("/{id}")
    public Khoa getKhoaById(@PathVariable long id) {
        return serviceKhoa.getKhoaById(id);
    }

    // Thêm mới 1 khoa
    @PostMapping("/add")
    public Khoa addKhoa(@RequestBody Khoa khoa) {
        return serviceKhoa.addKhoa(khoa);
    }

    // Cập nhật thông tin 1 khoa
    @PutMapping("/{id}")
    public Khoa updateKhoa(@PathVariable long id, @RequestBody Khoa khoa) {
        // Xóa khoa cũ nếu có, rồi thêm mới (vì đang dùng List tạm)
        serviceKhoa.deleteKhoa(id);
        khoa.setTtaMakh(id);
        return serviceKhoa.addKhoa(khoa);
    }

    // Xóa 1 khoa theo ID
    @DeleteMapping("/{id}")
    public boolean deleteKhoa(@PathVariable long id) {
        return serviceKhoa.deleteKhoa(id);
    }
}
