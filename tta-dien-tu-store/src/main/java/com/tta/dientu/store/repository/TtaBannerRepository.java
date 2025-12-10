package com.tta.dientu.store.repository;

import com.tta.dientu.store.entity.TtaBanner;
import com.tta.dientu.store.entity.TtaDanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TtaBannerRepository extends JpaRepository<TtaBanner, Integer> {

    // Tìm banner theo danh mục và trạng thái
    Optional<TtaBanner> findFirstByTtaDanhMucAndTtaTrangThaiTrue(TtaDanhMuc ttaDanhMuc);

    // Tìm banner chung (không thuộc danh mục nào) và trạng thái
    @Query("SELECT b FROM TtaBanner b WHERE b.ttaDanhMuc IS NULL AND b.ttaTrangThai = true")
    List<TtaBanner> findGeneralBanners();

    // Tìm tất cả banner đang active
    List<TtaBanner> findAllByTtaTrangThaiTrue();
}
