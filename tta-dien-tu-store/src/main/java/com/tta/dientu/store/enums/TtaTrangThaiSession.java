package com.tta.dientu.store.enums;

public enum TtaTrangThaiSession {
    PENDING, // Đang chờ thanh toán
    PAID, // Đã thanh toán (đơn hàng đã tạo)
    CANCELLED, // User hủy
    EXPIRED // Hết hạn
}
