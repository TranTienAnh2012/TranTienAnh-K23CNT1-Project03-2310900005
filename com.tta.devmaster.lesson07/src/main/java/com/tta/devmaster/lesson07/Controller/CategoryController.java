package com.tta.devmaster.lesson07.Controller;

import com.tta.devmaster.lesson07.entity.TtaCategory;
import com.tta.devmaster.lesson07.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
    @RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "category/category-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new TtaCategory());
        return "category/category-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id,  // <--- Long -> Integer
                               Model model) {
        model.addAttribute("category",
                categoryService.getCategoryById(id).orElse(null));
        return "category/category-form";
    }

    @PostMapping("/create")
    public String saveCategory(@ModelAttribute("category") TtaCategory category) {
        categoryService.saveCategory(category);
        return "redirect:/category";
    }

    @PostMapping("/create/{id}")
    public String updateCategory(@PathVariable Integer id,  // <--- Long -> Integer
                                 @ModelAttribute TtaCategory category) {
        category.setId(id);
        categoryService.saveCategory(category);
        return "redirect:/category";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Integer id) {  // <--- Long -> Integer
        categoryService.deleteCategory(id);
        return "redirect:/category";
    }
}
