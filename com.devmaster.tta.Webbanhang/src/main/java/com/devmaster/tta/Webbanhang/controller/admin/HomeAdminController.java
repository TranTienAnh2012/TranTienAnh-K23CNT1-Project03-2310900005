package com.devmaster.tta.Webbanhang.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDashboardController {

    @GetMapping("/admin")
    public String dashboard(Model model) {
        // TODO: Thêm thống kê: số user, đơn hàng, sản phẩm, doanh thu...
        return "admin/dashboard";
    }
}
