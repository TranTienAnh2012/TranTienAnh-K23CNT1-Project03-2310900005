package com.tta.dientu.store.service;

import com.tta.dientu.store.entity.TtaBanner;
import com.tta.dientu.store.entity.TtaDanhMuc;
import com.tta.dientu.store.repository.TtaBannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TtaBannerService {

    @Autowired
    private TtaBannerRepository ttaBannerRepository;

    public List<TtaBanner> getAllBanners() {
        return ttaBannerRepository.findAll();
    }

    public Optional<TtaBanner> getBannerById(Integer id) {
        return ttaBannerRepository.findById(id);
    }

    public TtaBanner saveBanner(TtaBanner ttaBanner) {
        return ttaBannerRepository.save(ttaBanner);
    }

    public void deleteBanner(Integer id) {
        ttaBannerRepository.deleteById(id);
    }

    public Optional<TtaBanner> getBannerByCategory(TtaDanhMuc ttaDanhMuc) {
        return ttaBannerRepository.findFirstByTtaDanhMucAndTtaTrangThaiTrue(ttaDanhMuc);
    }

    public List<TtaBanner> getGeneralBanners() {
        return ttaBannerRepository.findGeneralBanners();
    }

    public List<TtaBanner> getAllActiveBanners() {
        return ttaBannerRepository.findAllByTtaTrangThaiTrue();
    }
}
