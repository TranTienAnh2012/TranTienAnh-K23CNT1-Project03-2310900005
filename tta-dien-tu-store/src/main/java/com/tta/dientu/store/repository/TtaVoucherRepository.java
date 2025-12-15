package com.tta.dientu.store.repository;

import com.tta.dientu.store.entity.TtaVoucher;
import com.tta.dientu.store.enums.TtaVoucherStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TtaVoucherRepository extends JpaRepository<TtaVoucher, Integer> {

    /**
     * Find voucher by code
     */
    Optional<TtaVoucher> findByTtaCode(String code);

    /**
     * Find all vouchers by status
     */
    List<TtaVoucher> findByTtaStatus(TtaVoucherStatus status);

    /**
     * Find active vouchers that haven't expired
     */
    List<TtaVoucher> findByTtaStatusAndTtaEndDateAfter(TtaVoucherStatus status, LocalDateTime date);

    /**
     * Find all active and valid vouchers
     */
    @Query("SELECT v FROM TtaVoucher v WHERE v.ttaStatus = 'ACTIVE' " +
            "AND (v.ttaStartDate IS NULL OR v.ttaStartDate <= CURRENT_TIMESTAMP) " +
            "AND (v.ttaEndDate IS NULL OR v.ttaEndDate >= CURRENT_TIMESTAMP) " +
            "AND (v.ttaTotalQuantity IS NULL OR v.ttaUsedQuantity < v.ttaTotalQuantity)")
    List<TtaVoucher> findAllAvailableVouchers();

    /**
     * Check if code exists
     */
    boolean existsByTtaCode(String code);
}
