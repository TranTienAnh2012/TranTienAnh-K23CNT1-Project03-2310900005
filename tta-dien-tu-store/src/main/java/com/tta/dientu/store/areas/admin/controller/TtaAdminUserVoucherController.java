package com.tta.dientu.store.areas.admin.controller;

import com.tta.dientu.store.entity.TtaUserVoucher;
import com.tta.dientu.store.service.TtaUserVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/user-voucher")
@RequiredArgsConstructor
public class TtaAdminUserVoucherController {

    private final TtaUserVoucherService userVoucherService;

    @GetMapping
    public String list(Model model) {
        // Find all via service
        List<TtaUserVoucher> list = userVoucherService.getAllUserVouchers();
        model.addAttribute("listUserVoucher", list);
        model.addAttribute("pageTitle", "Quản lý User Voucher");
        return "areas/admin/TtaUserVoucher/tta-index";
    }
}
