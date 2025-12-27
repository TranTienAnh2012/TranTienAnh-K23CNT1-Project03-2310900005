package com.tta.dientu.store.enums;

public enum TtaTrangThaiThanhToanOnline {
    PENDING("Đang chờ thanh toán"),
    COMPLETED("Đã thanh toán"),
    EXPIRED("Hết hạn"),
    CANCELLED("Đã hủy");

    private final String tenHienThi;

    TtaTrangThaiThanhToanOnline(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}
