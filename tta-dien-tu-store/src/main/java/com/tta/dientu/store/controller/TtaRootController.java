package com.tta.dientu.store.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Root controller to handle default landing page
 */
@Controller
public class TtaRootController {

    /**
     * Redirect root URL to user product page
     * This ensures that when users visit the site, they land on the user product
     * page
     */
    @GetMapping("/")
    public String redirectToUserProducts() {
        return "redirect:/user/san-pham";
    }
}
