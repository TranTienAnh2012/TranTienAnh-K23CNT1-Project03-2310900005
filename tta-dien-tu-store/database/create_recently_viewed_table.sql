-- Tạo bảng tta_sanphamdaxem để lưu lịch sử xem sản phẩm
CREATE TABLE IF NOT EXISTS tta_sanphamdaxem (
    tta_Id INT AUTO_INCREMENT PRIMARY KEY,
    tta_MaNguoiDung INT NOT NULL,
    tta_MaSanPham INT NOT NULL,
    tta_NgayXem DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_product (tta_MaNguoiDung, tta_MaSanPham),
    FOREIGN KEY (tta_MaSanPham) REFERENCES tta_SanPham(tta_MaSanPham) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
