package com.devmaster.MonHoc.service;

import com.devmaster.MonHoc.entity.Monhoc;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service class: ServiceMonhoc
 * <p>Lớp dịch vụ thực hiện các chức năng thao tác với List Object Monhoc</p>
 * @Name Trần Tiến Anh
 * @Date 10/11/2025
 * @version 1.0
 */
@Service
public class serviceMonhoc  {
    private List<Monhoc> monhocs = new ArrayList<>();

    public serviceMonhoc () {
        monhocs.addAll(Arrays.asList(
                new Monhoc(1, "Nguyen Van A", "Nam", 25, 5000.0),
                new Monhoc(2, "Tran Thi B", "Nu", 30, 6000.0),
                new Monhoc(3, "Le Van C", "Nam", 28, 5500.0)
        ));
    }

    // Lấy toàn bộ danh sách
    public List<Monhoc> getAllMonhocs() {
        return monhocs;
    }

    // Lấy 1 Monhoc theo ID
    public Monhoc getMonhocById(long id) {
        return monhocs.stream().filter(m -> m.getTtaId() == id).findFirst().orElse(null);
    }

    // Thêm mới
    public Monhoc addMonhoc(Monhoc monhoc) {
        monhocs.add(monhoc);
        return monhoc;
    }

    // Xóa theo ID
    public boolean deleteMonhoc(long id) {
        Monhoc m = getMonhocById(id);
        return monhocs.remove(m);
    }

    // Cập nhật
    public Monhoc updateMonhoc(long id, Monhoc monhoc) {
        Monhoc existing = getMonhocById(id);
        if (existing == null) return null;
        existing.setTtaFullname(monhoc.getTtaFullname());
        existing.setTtaGender(monhoc.getTtaGender());
        existing.setTtaAge(monhoc.getTtaAge());
        existing.setTtaSalary(monhoc.getTtaSalary());
        return existing;
    }
}
