-- Sửa constraint cho column tta_ThuocTinhID để cho phép NULL
-- Chạy script này trong MySQL Workbench hoặc phpMyAdmin

ALTER TABLE tta_giatrithuoctinh 
MODIFY COLUMN tta_ThuocTinhID INT NULL;

-- Kiểm tra kết quả
DESCRIBE tta_giatrithuoctinh;
