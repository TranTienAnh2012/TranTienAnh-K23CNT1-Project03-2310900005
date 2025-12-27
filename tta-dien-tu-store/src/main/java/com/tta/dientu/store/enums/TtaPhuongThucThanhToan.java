package com.tta.dientu.store.enums;

public enum TtaPhuongThucThanhToan {
    COD("Thanh toán khi nhận hàng"),
    ONLINE("Thanh toán online");

    private final String tenHienThi;

    TtaPhuongThucThanhToan(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}
