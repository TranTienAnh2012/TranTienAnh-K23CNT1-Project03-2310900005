-- Tạo bảng tta_danhmucthuoctinh (Danh mục thuộc tính)
CREATE TABLE IF NOT EXISTS tta_danhmucthuoctinh (
    tta_id INT AUTO_INCREMENT PRIMARY KEY,
    tta_TenDanhMuc VARCHAR(100) NOT NULL,
    tta_MoTa TEXT,
    tta_ThuTu INT DEFAULT 0
);

-- Tạo bảng tta_giatrithuoctinh (Giá trị thuộc tính sản phẩm)
CREATE TABLE IF NOT EXISTS tta_giatrithuoctinh (
    tta_id INT AUTO_INCREMENT PRIMARY KEY,
    tta_MaSanPham INT NOT NULL,
    tta_ThuocTinh VARCHAR(100) NOT NULL,
    tta_GiaTri VARCHAR(255) NOT NULL,
    FOREIGN KEY (tta_MaSanPham) REFERENCES tta_SanPham(tta_MaSanPham) ON DELETE CASCADE
);

-- Insert dữ liệu mẫu cho danh mục thuộc tính
INSERT INTO tta_danhmucthuoctinh (tta_TenDanhMuc, tta_MoTa, tta_ThuTu) VALUES
('Cấu hình', 'Thông số cấu hình phần cứng', 1),
('Thiết kế', 'Thông số về thiết kế và kích thước', 2),
('Kết nối', 'Các cổng kết nối và tính năng không dây', 3),
('Pin & Sạc', 'Thông tin về pin và sạc', 4);

-- Insert dữ liệu mẫu thuộc tính cho sản phẩm (giả sử sản phẩm có ID = 1)
-- Bạn cần thay đổi tta_MaSanPham theo ID sản phẩm thực tế trong database
INSERT INTO tta_giatrithuoctinh (tta_MaSanPham, tta_ThuocTinh, tta_GiaTri) VALUES
(1, 'CPU', 'Intel Core i7-12700H'),
(1, 'RAM', '16GB DDR4'),
(1, 'Ổ cứng', '512GB SSD NVMe'),
(1, 'Màn hình', '15.6 inch Full HD (1920x1080)'),
(1, 'Card đồ họa', 'NVIDIA GeForce RTX 3050 4GB'),
(1, 'Hệ điều hành', 'Windows 11 Home'),
(1, 'Trọng lượng', '2.1 kg'),
(1, 'Pin', '56Wh, khoảng 6-8 giờ');
