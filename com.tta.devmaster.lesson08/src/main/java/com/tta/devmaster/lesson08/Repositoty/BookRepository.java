package com.tta.devmaster.lesson08.Repositoty;

import com.tta.devmaster.lesson08.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
}
