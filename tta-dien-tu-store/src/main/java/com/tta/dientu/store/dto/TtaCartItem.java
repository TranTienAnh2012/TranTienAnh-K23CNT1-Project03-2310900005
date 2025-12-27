package com.tta.dientu.store.dto;

import com.tta.dientu.store.entity.TtaSanPham;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TtaCartItem {
    private TtaSanPham ttaSanPham;
    private int ttaSoLuong;

    public double getThanhTien() {
        return ttaSanPham.getEffectivePrice().doubleValue() * ttaSoLuong;
    }
}
