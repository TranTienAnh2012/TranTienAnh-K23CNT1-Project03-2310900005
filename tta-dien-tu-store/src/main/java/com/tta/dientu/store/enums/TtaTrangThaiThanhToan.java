package com.tta.dientu.store.enums;

public enum TtaTrangThaiThanhToan {
    UNPAID("Chưa thanh toán"),
    PAID("Đã thanh toán"),
    REFUNDED("Đã hoàn tiền");

    private final String tenHienThi;

    TtaTrangThaiThanhToan(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}
