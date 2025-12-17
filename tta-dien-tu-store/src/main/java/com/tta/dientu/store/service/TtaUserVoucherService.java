package com.tta.dientu.store.service;

import com.tta.dientu.store.entity.TtaDonHang;
import com.tta.dientu.store.entity.TtaQuanTriVien;
import com.tta.dientu.store.entity.TtaUserVoucher;
import com.tta.dientu.store.entity.TtaVoucher;
import com.tta.dientu.store.repository.TtaQuanTriVienRepository;
import com.tta.dientu.store.repository.TtaUserVoucherRepository;
import com.tta.dientu.store.repository.TtaVoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service for user voucher operations (claim, use, validate)
 */
@Service
@RequiredArgsConstructor
public class TtaUserVoucherService {

    private final TtaVoucherRepository voucherRepository;
    private final TtaUserVoucherRepository userVoucherRepository;
    private final TtaQuanTriVienRepository quantriVienRepository;

    /**
     * Get all user vouchers (for Admin)
     */
    public List<TtaUserVoucher> getAllUserVouchers() {
        return userVoucherRepository.findAll(
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "ttaId"));
    }

    /**
     * Get all vouchers available for users to claim
     */
    public List<TtaVoucher> getAvailableVouchers() {
        return voucherRepository.findAllAvailableVouchers();
    }

    /**
     * Get all vouchers claimed by a user
     */
    public List<TtaUserVoucher> getMyVouchers(Integer userId) {
        return userVoucherRepository.findByTtaUser_TtaMaNguoiDung(userId);
    }

    /**
     * Get all valid (unused and not expired) vouchers for a user
     */
    public List<TtaUserVoucher> getMyValidVouchers(Integer userId) {
        return userVoucherRepository.findValidVouchersByUserId(userId);
    }

    /**
     * Claim a voucher for a user
     */
    @Transactional
    public TtaUserVoucher claimVoucher(Integer userId, String voucherCode) {
        // Find voucher
        TtaVoucher voucher = voucherRepository.findByTtaCode(voucherCode)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại: " + voucherCode));

        return claimVoucherProcess(userId, voucher);
    }

    /**
     * Claim a voucher by ID
     */
    @Transactional
    public TtaUserVoucher claimVoucherById(Integer userId, Integer voucherId) {
        // Find voucher
        TtaVoucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));

        return claimVoucherProcess(userId, voucher);
    }

    private TtaUserVoucher claimVoucherProcess(Integer userId, TtaVoucher voucher) {
        // Check if voucher is valid
        if (!voucher.canBeClaimed()) {
            throw new RuntimeException("Voucher không khả dụng hoặc đã hết lượt");
        }

        // Check user claim limit
        Long claimCount = userVoucherRepository.countByTtaUser_TtaMaNguoiDungAndTtaVoucher_TtaId(userId,
                voucher.getTtaId());
        if (voucher.getTtaLimitPerUser() != null && claimCount >= voucher.getTtaLimitPerUser()) {
            throw new RuntimeException("Bạn đã nhận tối đa " + voucher.getTtaLimitPerUser() + " lần voucher này");
        }

        // Check if already claimed
        if (userVoucherRepository.existsByTtaUser_TtaMaNguoiDungAndTtaVoucher_TtaId(userId, voucher.getTtaId())) {
            throw new RuntimeException("Bạn đã nhận voucher này rồi");
        }

        // Find user
        TtaQuanTriVien user = quantriVienRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create user voucher
        TtaUserVoucher userVoucher = new TtaUserVoucher();
        userVoucher.setTtaUser(user);
        userVoucher.setTtaVoucher(voucher);

        return userVoucherRepository.save(userVoucher);
    }

    /**
     * Validate if user can use a voucher for an order
     */
    public boolean validateVoucher(Integer userId, String voucherCode, BigDecimal orderTotal) {
        try {
            // Find user voucher
            TtaUserVoucher userVoucher = userVoucherRepository
                    .findByTtaUser_TtaMaNguoiDungAndTtaVoucher_TtaCodeAndTtaIsUsed(userId, voucherCode, false)
                    .orElseThrow(() -> new RuntimeException("Bạn chưa có voucher này hoặc đã sử dụng"));

            // Check if valid
            if (!userVoucher.isValid()) {
                throw new RuntimeException("Voucher đã hết hạn hoặc không hợp lệ");
            }

            TtaVoucher voucher = userVoucher.getTtaVoucher();

            // Check minimum order value
            if (voucher.getTtaMinOrderValue() != null &&
                    orderTotal.compareTo(voucher.getTtaMinOrderValue()) < 0) {
                throw new RuntimeException("Đơn hàng chưa đủ giá trị tối thiểu: " +
                        voucher.getTtaMinOrderValue() + "₫");
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Calculate discount amount for an order
     */
    public BigDecimal calculateDiscount(Integer userId, String voucherCode, BigDecimal orderTotal) {
        // Find user voucher
        TtaUserVoucher userVoucher = userVoucherRepository
                .findByTtaUser_TtaMaNguoiDungAndTtaVoucher_TtaCodeAndTtaIsUsed(userId, voucherCode, false)
                .orElseThrow(() -> new RuntimeException("Bạn chưa có voucher này hoặc đã sử dụng"));

        // Check if valid
        if (!userVoucher.isValid()) {
            throw new RuntimeException("Voucher đã hết hạn hoặc không hợp lệ");
        }

        TtaVoucher voucher = userVoucher.getTtaVoucher();

        // Calculate discount using voucher's helper method
        return voucher.calculateDiscount(orderTotal);
    }

    /**
     * Use a voucher for an order
     */
    @Transactional
    public void useVoucher(Integer userId, String voucherCode, TtaDonHang order) {
        // Find user voucher
        TtaUserVoucher userVoucher = userVoucherRepository
                .findByTtaUser_TtaMaNguoiDungAndTtaVoucher_TtaCodeAndTtaIsUsed(userId, voucherCode, false)
                .orElseThrow(() -> new RuntimeException("Bạn chưa có voucher này hoặc đã sử dụng"));

        // Check if valid
        if (!userVoucher.isValid()) {
            throw new RuntimeException("Voucher đã hết hạn hoặc không hợp lệ");
        }

        // Mark as used
        userVoucher.markAsUsed(order);
        userVoucherRepository.save(userVoucher);
    }

    /**
     * Get user voucher by code
     */
    public TtaUserVoucher getUserVoucherByCode(Integer userId, String voucherCode) {
        return userVoucherRepository
                .findByTtaUser_TtaMaNguoiDungAndTtaVoucher_TtaCodeAndTtaIsUsed(userId, voucherCode, false)
                .orElseThrow(() -> new RuntimeException("Voucher not found or already used"));
    }
}
