package com.tta.dientu.store.enums;

/**
 * Enum định nghĩa các trạng thái của đơn hàng
 */
public enum TtaTrangThaiDonHang {
    DA_DAT("Đã đặt", "warning", "bi-clock-history"),
    DANG_XU_LY("Đang xử lý", "info", "bi-hourglass-split"),
    DANG_GIAO("Đang giao", "primary", "bi-truck"),
    DA_GIAO("Đã giao", "success", "bi-check-circle"),
    DA_HUY("Đã hủy", "danger", "bi-x-circle");

    private final String tenHienThi;
    private final String mauBadge; // Bootstrap color class
    private final String icon; // Bootstrap icon class

    TtaTrangThaiDonHang(String tenHienThi, String mauBadge, String icon) {
        this.tenHienThi = tenHienThi;
        this.mauBadge = mauBadge;
        this.icon = icon;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }

    public String getMauBadge() {
        return mauBadge;
    }

    public String getIcon() {
        return icon;
    }

    /**
     * Lấy class CSS đầy đủ cho badge
     */
    public String getBadgeClass() {
        return "bg-" + mauBadge;
    }

    /**
     * Kiểm tra xem đơn hàng có thể hủy không
     */
    public boolean coTheHuy() {
        return this == DA_DAT || this == DANG_XU_LY;
    }

    /**
     * Kiểm tra xem đơn hàng có thể chuyển sang trạng thái tiếp theo không
     */
    public boolean coTheChuyenSang(TtaTrangThaiDonHang trangThaiMoi) {
        switch (this) {
            case DA_DAT:
                return trangThaiMoi == DANG_XU_LY || trangThaiMoi == DA_HUY;
            case DANG_XU_LY:
                return trangThaiMoi == DANG_GIAO || trangThaiMoi == DA_HUY;
            case DANG_GIAO:
                return trangThaiMoi == DA_GIAO;
            case DA_GIAO:
            case DA_HUY:
                return false; // Không thể chuyển từ trạng thái cuối
            default:
                return false;
        }
    }

    /**
     * Lấy trạng thái tiếp theo trong quy trình
     */
    public TtaTrangThaiDonHang getTrangThaiTiepTheo() {
        switch (this) {
            case DA_DAT:
                return DANG_XU_LY;
            case DANG_XU_LY:
                return DANG_GIAO;
            case DANG_GIAO:
                return DA_GIAO;
            default:
                return null;
        }
    }
}
