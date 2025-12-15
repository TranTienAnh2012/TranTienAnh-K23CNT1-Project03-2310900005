-- ===================================
-- SQL Script to Drop Old Voucher Tables
-- ===================================
-- Run this script BEFORE restarting the application
-- This will remove old voucher structure from database

-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- Drop old voucher-related tables
DROP TABLE IF EXISTS tta_SanPhamKhuyenMai;
DROP TABLE IF EXISTS tta_NguoiDungGiamGia;
DROP TABLE IF EXISTS tta_KhuyenMai;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- ===================================
-- After running this script:
-- 1. Restart your Spring Boot application
-- 2. JPA will auto-create new tables:
--    - tta_Voucher
--    - tta_UserVoucher
-- ===================================
