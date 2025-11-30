package com.tta.dientu.store.areas.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TtaLoginRedirectController {

    // Redirect từ /login đến /account/login
    @GetMapping("/login")
    public String redirectToAccountLogin() {
        return "redirect:/account/login";
    }

    // Redirect từ /register đến /account/register
    @GetMapping("/register")
    public String redirectToAccountRegister() {
        return "redirect:/account/register";
    }
}

