package com.tta.dientu.store.areas.user.controller;

import com.tta.dientu.store.service.TtaCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user/cart")
@RequiredArgsConstructor
public class TtaCartController {
    private final TtaCartService ttaCartService;

    @GetMapping
    public String viewCart(Model model) {
        model.addAttribute("ttaCartItems", ttaCartService.getCartItems());
        model.addAttribute("total", ttaCartService.getTotal());
        model.addAttribute("pageTitle", "Giỏ hàng - TTA Store");
        return "areas/user/cart/tta-list";
    }

    @GetMapping("/add/{id}")
    public String addToCart(@PathVariable("id") Integer id,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity) {
        try {
            ttaCartService.addToCart(id, quantity);
        } catch (RuntimeException e) {
            return "redirect:/account/login";
        }
        return "redirect:/user/cart";
    }

    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable("id") Integer id) {
        ttaCartService.removeFromCart(id);
        return "redirect:/user/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam("id") Integer id, @RequestParam("quantity") int quantity) {
        ttaCartService.updateQuantity(id, quantity);
        return "redirect:/user/cart";
    }
}
