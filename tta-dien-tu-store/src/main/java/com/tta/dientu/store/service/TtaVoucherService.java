package com.tta.dientu.store.service;

import com.tta.dientu.store.entity.TtaVoucher;
import com.tta.dientu.store.enums.TtaVoucherStatus;
import com.tta.dientu.store.repository.TtaVoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for admin voucher management
 */
@Service
@RequiredArgsConstructor
public class TtaVoucherService {

    private final TtaVoucherRepository voucherRepository;

    /**
     * Get all vouchers
     */
    public List<TtaVoucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    /**
     * Get voucher by ID
     */
    public TtaVoucher getVoucherById(Integer id) {
        return voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found with id: " + id));
    }

    /**
     * Get voucher by code
     */
    public TtaVoucher getVoucherByCode(String code) {
        return voucherRepository.findByTtaCode(code)
                .orElseThrow(() -> new RuntimeException("Voucher not found with code: " + code));
    }

    /**
     * Create new voucher
     */
    @Transactional
    public TtaVoucher createVoucher(TtaVoucher voucher) {
        // Validate code uniqueness
        if (voucherRepository.existsByTtaCode(voucher.getTtaCode())) {
            throw new RuntimeException("Voucher code already exists: " + voucher.getTtaCode());
        }

        // Validate dates
        if (voucher.getTtaStartDate() != null && voucher.getTtaEndDate() != null) {
            if (voucher.getTtaStartDate().isAfter(voucher.getTtaEndDate())) {
                throw new RuntimeException("Start date must be before end date");
            }
        }

        return voucherRepository.save(voucher);
    }

    /**
     * Update voucher
     */
    @Transactional
    public TtaVoucher updateVoucher(Integer id, TtaVoucher updatedVoucher) {
        TtaVoucher existingVoucher = getVoucherById(id);

        // Check if code is being changed and if new code already exists
        if (!existingVoucher.getTtaCode().equals(updatedVoucher.getTtaCode())) {
            if (voucherRepository.existsByTtaCode(updatedVoucher.getTtaCode())) {
                throw new RuntimeException("Voucher code already exists: " + updatedVoucher.getTtaCode());
            }
        }

        // Update fields
        existingVoucher.setTtaCode(updatedVoucher.getTtaCode());
        existingVoucher.setTtaName(updatedVoucher.getTtaName());
        existingVoucher.setTtaDescription(updatedVoucher.getTtaDescription());
        existingVoucher.setTtaDiscountType(updatedVoucher.getTtaDiscountType());
        existingVoucher.setTtaDiscountValue(updatedVoucher.getTtaDiscountValue());
        existingVoucher.setTtaMaxDiscount(updatedVoucher.getTtaMaxDiscount());
        existingVoucher.setTtaMinOrderValue(updatedVoucher.getTtaMinOrderValue());
        existingVoucher.setTtaTotalQuantity(updatedVoucher.getTtaTotalQuantity());
        existingVoucher.setTtaLimitPerUser(updatedVoucher.getTtaLimitPerUser());
        existingVoucher.setTtaStartDate(updatedVoucher.getTtaStartDate());
        existingVoucher.setTtaEndDate(updatedVoucher.getTtaEndDate());
        existingVoucher.setTtaStatus(updatedVoucher.getTtaStatus());
        existingVoucher.setTtaApplyToAll(updatedVoucher.getTtaApplyToAll());
        existingVoucher.setTtaProductIds(updatedVoucher.getTtaProductIds());
        existingVoucher.setTtaCategoryIds(updatedVoucher.getTtaCategoryIds());

        return voucherRepository.save(existingVoucher);
    }

    /**
     * Delete voucher
     */
    @Transactional
    public void deleteVoucher(Integer id) {
        TtaVoucher voucher = getVoucherById(id);
        voucherRepository.delete(voucher);
    }

    /**
     * Toggle voucher status (activate/deactivate)
     */
    @Transactional
    public TtaVoucher toggleStatus(Integer id) {
        TtaVoucher voucher = getVoucherById(id);

        if (voucher.getTtaStatus() == TtaVoucherStatus.ACTIVE) {
            voucher.setTtaStatus(TtaVoucherStatus.INACTIVE);
        } else {
            voucher.setTtaStatus(TtaVoucherStatus.ACTIVE);
        }

        return voucherRepository.save(voucher);
    }

    /**
     * Get all active vouchers
     */
    public List<TtaVoucher> getActiveVouchers() {
        return voucherRepository.findByTtaStatus(TtaVoucherStatus.ACTIVE);
    }

    /**
     * Get all available vouchers (active and not expired)
     */
    public List<TtaVoucher> getAvailableVouchers() {
        return voucherRepository.findAllAvailableVouchers();
    }

    /**
     * Auto-expire vouchers that have passed their end date
     */
    @Transactional
    public void autoExpireVouchers() {
        LocalDateTime now = LocalDateTime.now();
        List<TtaVoucher> activeVouchers = voucherRepository.findByTtaStatus(TtaVoucherStatus.ACTIVE);

        for (TtaVoucher voucher : activeVouchers) {
            if (voucher.getTtaEndDate() != null && now.isAfter(voucher.getTtaEndDate())) {
                voucher.setTtaStatus(TtaVoucherStatus.EXPIRED);
                voucherRepository.save(voucher);
            }
        }
    }
}
