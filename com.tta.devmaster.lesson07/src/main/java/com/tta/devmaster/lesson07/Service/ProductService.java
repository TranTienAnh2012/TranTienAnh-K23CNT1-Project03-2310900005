package com.tta.devmaster.lesson07.Service;

import com.tta.devmaster.lesson07.entity.Product;
import com.tta.devmaster.lesson07.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Đọc toàn bộ dữ liệu bảng Product
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Đọc dữ liệu bảng Product theo id
    public Optional<Product> findById(Integer id) {  // <--- đổi Long -> Integer
        return productRepository.findById(id);
    }

    // Cập nhật: create / update
    public Product saveProduct(Product product) {
        System.out.println(product);
        return productRepository.save(product);
    }

    // Xóa product theo id
    public void deleteProduct(Integer id) {  // <--- đổi Long -> Integer
        productRepository.deleteById(id);
    }
}
