package com.tta.dientu.store.config;

import com.tta.dientu.store.areas.user.service.TtaCustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class TtaSecurityConfig {

    private final TtaCustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Sử dụng NoOpPasswordEncoder để so sánh plain text (không mã hóa)
        // CẢNH BÁO: Chỉ dùng cho môi trường phát triển, không dùng cho production!
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * Cấu hình DaoAuthenticationProvider để Spring Security sử dụng:
     * - CustomUserDetailsService: Load user từ bảng QuanTriVien
     * - PasswordEncoder: So sánh mật khẩu plain text
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - không cần đăng nhập
                        .requestMatchers("/", "/login", "/register", "/account/login",
                                "/account/register",
                                "/account/forgot-password",
                                "/css/**", "/js/**", "/images/**", "/uploads/**",
                                "/webjars/**",
                                "/user/san-pham", "/user/san-pham/**",
                                "/user/dashboard")
                        .permitAll()

                        // Admin endpoints - chỉ ADMIN mới truy cập được
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // User endpoints - cần đăng nhập (USER hoặc ADMIN)
                        .requestMatchers("/user/**", "/account/profile").authenticated()

                        // Các request khác - cho phép tất cả (có thể điều chỉnh sau)
                        .anyRequest().permitAll())
                .formLogin(form -> form
                        // Trang đăng nhập
                        .loginPage("/account/login")
                        // URL xử lý đăng nhập (Spring Security tự động xử lý)
                        .loginProcessingUrl("/account/login")
                        // Sau khi đăng nhập thành công -> redirect đến method kiểm tra role
                        // Flow: Login thành công -> /account/redirect-by-role -> kiểm tra role
                        // ->
                        // redirect đến /admin/dashboard hoặc /user/dashboard
                        .defaultSuccessUrl("/account/redirect-by-role", true)
                        // Nếu đăng nhập thất bại (sai email/mật khẩu hoặc tài khoản không tồn
                        // tại)
                        .failureUrl("/account/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/account/logout")
                        .logoutSuccessUrl("/account/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/account/access-denied"))
                .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
