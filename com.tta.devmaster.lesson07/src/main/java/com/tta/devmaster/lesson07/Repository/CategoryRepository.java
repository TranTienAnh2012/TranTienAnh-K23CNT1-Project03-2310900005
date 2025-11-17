package com.tta.devmaster.lesson07.Repository;

import com.tta.devmaster.lesson07.entity.TtaCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<TtaCategory, Integer> {
}
