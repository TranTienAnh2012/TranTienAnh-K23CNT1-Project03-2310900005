package com.tta.dientu.store.areas.user.service;

import com.tta.dientu.store.entity.TtaQuanTriVien;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class TtaCustomUserDetails implements UserDetails {

    private final TtaQuanTriVien ttaQuanTriVien;

    public TtaCustomUserDetails(TtaQuanTriVien ttaQuanTriVien) {
        this.ttaQuanTriVien = ttaQuanTriVien;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Phân quyền dựa trên vaiTro: 1 = ADMIN, 0 = USER
        if (ttaQuanTriVien.getTtaVaiTro() != null && ttaQuanTriVien.getTtaVaiTro() == 1) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return ttaQuanTriVien.getTtaMatKhau();
    }

    @Override
    public String getUsername() {
        return ttaQuanTriVien.getTtaEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Getter để truy cập thông tin user đầy đủ
    public TtaQuanTriVien getQuanTriVien() {
        return ttaQuanTriVien;
    }

    public String getHoTen() {
        return ttaQuanTriVien.getTtaHoTen();
    }

    public Integer getVaiTro() {
        return ttaQuanTriVien.getTtaVaiTro();
    }

    public Integer getTtaMaNguoiDung() {
        return ttaQuanTriVien.getTtaMaNguoiDung();
    }
}
