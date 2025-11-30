package k23cnt1.tvclesson05.controller;

import k23cnt1.tvclesson05.entity.Info;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping
public class HomeController {

    @GetMapping
    public String index() {
        return "index";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        List<Info> profile = new ArrayList<>();
        // Tạo thông tin profile
        profile.add(new Info("Devmaster Academy",
                "dev",
                "contact@devmaster.edu.vn",
                        "https://devmaster.edu.vn"));

        model.addAttribute("tvcPrfile", profile);
        return "profile";
    }
}
