package com.tta.dientu.store.config;

import com.tta.dientu.store.entity.TtaQuanTriVien;
import com.tta.dientu.store.repository.TtaQuanTriVienRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class TtaDataSeeder {

    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.context.ApplicationContext context;

    @Bean
    public CommandLineRunner demoData(TtaQuanTriVienRepository repo) {
        return args -> {
            Optional<TtaQuanTriVien> admin = repo.findByTtaEmail("admin@gmail.com");
            if (admin.isEmpty()) {
                TtaQuanTriVien newAdmin = new TtaQuanTriVien();
                newAdmin.setTtaEmail("admin@gmail.com");
                newAdmin.setTtaHoTen("Administrator");
                newAdmin.setTtaMatKhau("123456");
                newAdmin.setTtaVaiTro(1); // 1 = Admin
                repo.save(newAdmin);
                System.out.println(">>> SEEDER: Created Admin user (admin@gmail.com / 123456)");
            } else {
                System.out.println(">>> SEEDER: Admin user already exists.");
            }

            // Seed Banners
            com.tta.dientu.store.repository.TtaBannerRepository bannerRepo = context
                    .getBean(com.tta.dientu.store.repository.TtaBannerRepository.class);

            System.out.println(">>> SEEDER: Current banner count: " + bannerRepo.count());

            if (bannerRepo.count() == 0) {
                com.tta.dientu.store.entity.TtaBanner b1 = new com.tta.dientu.store.entity.TtaBanner();
                b1.setTtaTenBanner("Big Sale 2024");
                b1.setTtaNoiDung("Giảm giá cực sốc lên đến 50% cho toàn bộ laptop");
                b1.setTtaHinhAnh("banner_demo_1.jpg"); // Dummy image name
                b1.setTtaTrangThai(true);
                bannerRepo.save(b1);

                com.tta.dientu.store.entity.TtaBanner b2 = new com.tta.dientu.store.entity.TtaBanner();
                b2.setTtaTenBanner("iPhone 15 Series");
                b2.setTtaNoiDung("Sẵn hàng đủ màu, giao ngay trong 1h");
                b2.setTtaHinhAnh("banner_demo_2.jpg");
                b2.setTtaTrangThai(true);
                bannerRepo.save(b2);

                System.out.println(">>> SEEDER: Created 2 demo banners.");
            }
        };
    }
}
