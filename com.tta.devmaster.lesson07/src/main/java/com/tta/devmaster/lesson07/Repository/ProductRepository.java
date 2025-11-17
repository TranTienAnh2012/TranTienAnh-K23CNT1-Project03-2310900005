package com.tta.devmaster.lesson07.Repository;

import com.tta.devmaster.lesson07.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
}
