package com.devmaster.lesson05.controller;

import com.devmaster.lesson05.entity.Info;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/") // root path
public class HomeController {

    @GetMapping
    public String index() {
        return "index"; // trả về file index.html
    }

    @GetMapping("/profile")
    public String profile(Model model){
        // Tạo danh sách profile
        List<Info> profile = new ArrayList<>();
        profile.add(new Info(
                "Devmaster Academy Trần Tiến Anh",
                "dev",
                "contact@devmaster.edu.vn",
                "https://devmaster.edu.vn"
        ));

        // Đưa profile vào model với key "DevmasterProfile"
        model.addAttribute("DevmasterProfile", profile);

        return "profile"; // trả về file profile.html
    }
}
