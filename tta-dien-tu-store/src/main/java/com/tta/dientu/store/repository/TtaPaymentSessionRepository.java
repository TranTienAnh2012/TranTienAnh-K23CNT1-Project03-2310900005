package com.tta.dientu.store.repository;

import com.tta.dientu.store.entity.TtaPaymentSession;
import com.tta.dientu.store.enums.TtaTrangThaiSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TtaPaymentSessionRepository extends JpaRepository<TtaPaymentSession, Integer> {

    /**
     * Tìm session theo trạng thái và ngày hết hạn
     */
    List<TtaPaymentSession> findByTtaTrangThaiAndTtaNgayHetHanBefore(
            TtaTrangThaiSession ttaTrangThai,
            LocalDateTime ngayHetHan);

    /**
     * Tìm session theo nội dung chuyển khoản
     */
    Optional<TtaPaymentSession> findByTtaNoiDungCK(String noiDungCK);
}
