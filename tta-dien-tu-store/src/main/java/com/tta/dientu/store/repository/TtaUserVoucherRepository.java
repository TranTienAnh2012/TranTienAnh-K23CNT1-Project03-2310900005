package com.tta.dientu.store.repository;

import com.tta.dientu.store.entity.TtaUserVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TtaUserVoucherRepository extends JpaRepository<TtaUserVoucher, Integer> {

    /**
     * Find all vouchers claimed by a user
     */
    List<TtaUserVoucher> findByTtaUser_TtaMaNguoiDung(Integer userId);

    /**
     * Find user's vouchers by usage status
     */
    List<TtaUserVoucher> findByTtaUser_TtaMaNguoiDungAndTtaIsUsed(Integer userId, Boolean isUsed);

    /**
     * Find specific user voucher by code and usage status
     */
    Optional<TtaUserVoucher> findByTtaUser_TtaMaNguoiDungAndTtaVoucher_TtaCodeAndTtaIsUsed(
            Integer userId, String voucherCode, Boolean isUsed);

    /**
     * Count how many times user has claimed a specific voucher
     */
    Long countByTtaUser_TtaMaNguoiDungAndTtaVoucher_TtaId(Integer userId, Integer voucherId);

    /**
     * Find all valid (unused and not expired) vouchers for a user
     */
    @Query("SELECT uv FROM TtaUserVoucher uv WHERE uv.ttaUser.ttaMaNguoiDung = :userId " +
            "AND uv.ttaIsUsed = false " +
            "AND (uv.ttaExpiresAt IS NULL OR uv.ttaExpiresAt >= CURRENT_TIMESTAMP)")
    List<TtaUserVoucher> findValidVouchersByUserId(@Param("userId") Integer userId);

    /**
     * Check if user has already claimed a voucher
     */
    boolean existsByTtaUser_TtaMaNguoiDungAndTtaVoucher_TtaId(Integer userId, Integer voucherId);
}
