package com.devmaster.ttaKhoa.service;

import com.devmaster.ttaKhoa.entity.Khoa;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service class: ServiceKhoa
 * <p>Lớp dịch vụ thực hiện các chức năng thao tác với List Object Khoa</p>
 * @Name Trần Tiến Anh
 * @Date 10/11/2025
 * @version 1.1
 */
@Service
public class ServiceKhoa {
    private List<Khoa> khoas = new ArrayList<>();

    public ServiceKhoa() {
        khoas.addAll(Arrays.asList(
                new Khoa(1, "Công nghệ thông tin"),
                new Khoa(2, "Thiết kế đồ họa"),
                new Khoa(3, "Quản trị kinh doanh")
        ));
    }

    public List<Khoa> getAllKhoas() {
        return khoas;
    }

    public Khoa getKhoaById(long id) {
        return khoas.stream().filter(k -> k.getTtaMakh() == id).findFirst().orElse(null);
    }

    public Khoa addKhoa(Khoa khoa) {
        khoas.add(khoa);
        return khoa;
    }

    public boolean deleteKhoa(long id) {
        Khoa k = getKhoaById(id);
        return khoas.remove(k);
    }
}
