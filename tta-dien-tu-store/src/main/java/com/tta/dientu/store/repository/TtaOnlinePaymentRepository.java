package com.tta.dientu.store.repository;

import com.tta.dientu.store.entity.TtaOnlinePayment;
import com.tta.dientu.store.enums.TtaTrangThaiThanhToanOnline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TtaOnlinePaymentRepository extends JpaRepository<TtaOnlinePayment, Integer> {

    Optional<TtaOnlinePayment> findByTtaDonHang_TtaMaDonHang(Integer orderId);

    List<TtaOnlinePayment> findByTtaTrangThaiAndTtaNgayTaoAfter(
            TtaTrangThaiThanhToanOnline status,
            LocalDateTime date);

    List<TtaOnlinePayment> findByTtaTrangThaiAndTtaNgayHetHanBefore(
            TtaTrangThaiThanhToanOnline status,
            LocalDateTime date);
}
