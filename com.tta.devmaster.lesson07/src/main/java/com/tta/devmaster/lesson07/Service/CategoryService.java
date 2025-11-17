package com.tta.devmaster.lesson07.Service;

import com.tta.devmaster.lesson07.entity.TtaCategory;
import com.tta.devmaster.lesson07.Repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Lấy danh sách tất cả category
    public List<TtaCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Lấy category theo ID
    public Optional<TtaCategory> getCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }

    // Lưu hoặc cập nhật category
    public TtaCategory saveCategory(TtaCategory category) {
        return categoryRepository.save(category);
    }

    // Xóa category theo ID
    public void deleteCategory(Integer id) {
        categoryRepository.deleteById(id);
    }
}
