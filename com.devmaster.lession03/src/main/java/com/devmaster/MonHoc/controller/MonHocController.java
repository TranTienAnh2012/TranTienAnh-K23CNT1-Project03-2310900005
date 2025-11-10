package com.devmaster.MonHoc.controller;

import com.devmaster.MonHoc.entity.Monhoc;
import com.devmaster.MonHoc.service.serviceMonhoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class: MonHocController
 * <p>Thực hiện các API REST cho đối tượng Monhoc</p>
 * @Name Trần Tiến Anh
 * @Date 10/11/2025
 * @version 1.1
 */
@RestController
@RequestMapping("/monhoc") // endpoint chính: /monhoc/...
public class MonHocController {

    @Autowired
    private serviceMonhoc serviceMonhoc;

    // Lấy toàn bộ danh sách môn học
    @GetMapping("/list")
    public List<Monhoc> getAllMonhocs() {
        return serviceMonhoc.getAllMonhocs();
    }

    // Lấy thông tin 1 môn học theo ID
    @GetMapping("/{id}")
    public Monhoc getMonhocById(@PathVariable long id) {
        return serviceMonhoc.getMonhocById(id);
    }

    // Thêm mới 1 môn học
    @PostMapping("/add")
    public Monhoc addMonhoc(@RequestBody Monhoc monhoc) {
        return serviceMonhoc.addMonhoc(monhoc);
    }

    // Cập nhật thông tin 1 môn học
    @PutMapping("/{id}")
    public Monhoc updateMonhoc(@PathVariable long id, @RequestBody Monhoc monhoc) {
        return serviceMonhoc.updateMonhoc(id, monhoc);
    }

    // Xóa 1 môn học theo ID
    @DeleteMapping("/{id}")
    public boolean deleteMonhoc(@PathVariable long id) {
        return serviceMonhoc.deleteMonhoc(id);
    }
}
