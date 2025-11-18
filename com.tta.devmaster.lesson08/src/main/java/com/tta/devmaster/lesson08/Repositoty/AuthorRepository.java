package com.tta.devmaster.lesson08.Repositoty;

import com.tta.devmaster.lesson08.entity.Author;
import
        org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface AuthorRepository extends
        JpaRepository<Author,Integer> {

}