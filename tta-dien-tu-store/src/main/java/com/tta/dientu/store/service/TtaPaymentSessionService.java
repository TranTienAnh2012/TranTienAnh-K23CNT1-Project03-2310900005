package com.tta.dientu.store.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tta.dientu.store.dto.TtaCartItem;
import com.tta.dientu.store.entity.TtaDonHang;
import com.tta.dientu.store.entity.TtaPaymentSession;
import com.tta.dientu.store.enums.TtaTrangThaiSession;
import com.tta.dientu.store.repository.TtaPaymentSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class TtaPaymentSessionService {

    private final TtaPaymentSessionRepository sessionRepository;
    private final TtaCartService cartService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    // Constructor to configure ObjectMapper
    public TtaPaymentSessionService(
            TtaPaymentSessionRepository sessionRepository,
            TtaCartService cartService) {
        this.sessionRepository = sessionRepository;
        this.cartService = cartService;
        this.objectMapper = new ObjectMapper();

        // Register JavaTimeModule for LocalDateTime support
        this.objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        this.objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Ignore lazy-loaded properties to avoid Hibernate proxy issues
        this.objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    @Value("${vpbank.api.url:http://localhost:5000}")
    private String vpbankApiUrl;

    @Value("${vpbank.account.number:0387742492}")
    private String accountNumber;

    @Value("${vpbank.account.name:TRAN TIEN ANH}")
    private String accountName;

    @Value("${vpbank.bank.code:VPB}")
    private String bankCode;

    /**
     * Tạo payment session từ thông tin checkout
     */
    @Transactional
    public TtaPaymentSession createSession(
            String hoTen,
            String soDienThoai,
            String diaChi,
            String email,
            String ghiChu,
            String voucherCode) {
        // Lấy cart từ session
        List<TtaCartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }

        // Tính tổng tiền
        BigDecimal tongTien = BigDecimal.valueOf(cartService.getTotal());
        BigDecimal giamGia = BigDecimal.ZERO;

        // Áp dụng voucher nếu có
        if (voucherCode != null && !voucherCode.isEmpty()) {
            // Voucher will be applied when creating the actual order from session
            // We store the voucher code in session for later use
        }

        // Serialize cart data to JSON
        String cartDataJson;
        try {
            cartDataJson = objectMapper.writeValueAsString(cartItems);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi khi lưu thông tin giỏ hàng", e);
        }

        // Tạo session
        TtaPaymentSession session = new TtaPaymentSession();
        session.setTtaHoTen(hoTen);
        session.setTtaSoDienThoai(soDienThoai);
        session.setTtaDiaChi(diaChi);
        session.setTtaEmail(email);
        session.setTtaGhiChu(ghiChu);
        session.setTtaCartDataJson(cartDataJson);
        session.setTtaTongTien(tongTien);
        session.setTtaVoucherCode(voucherCode);
        session.setTtaGiamGia(giamGia);
        session.setTtaTrangThai(TtaTrangThaiSession.PENDING);

        // Set timestamps
        LocalDateTime now = LocalDateTime.now();
        session.setTtaNgayTao(now);
        session.setTtaNgayCapNhat(now);
        session.setTtaNgayHetHan(now.plusMinutes(15)); // 15 minutes timeout

        // Save để có ID
        session = sessionRepository.save(session);

        // Tạo nội dung CK và QR code
        String noiDungCK = "TTA" + session.getTtaMaSession();
        session.setTtaNoiDungCK(noiDungCK);

        String qrUrl = generateQRCodeUrl(accountNumber, tongTien, noiDungCK);
        session.setTtaQrCodeUrl(qrUrl);

        return sessionRepository.save(session);
    }

    /**
     * Tạo URL QR code từ VietQR
     */
    private String generateQRCodeUrl(String accountNo, BigDecimal amount, String content) {
        try {
            String encodedContent = URLEncoder.encode(content, StandardCharsets.UTF_8.toString());
            String encodedAccountName = URLEncoder.encode(accountName, StandardCharsets.UTF_8.toString());

            return String.format(
                    "https://img.vietqr.io/image/%s-%s-compact2.jpg?amount=%s&addInfo=%s&accountName=%s",
                    bankCode,
                    accountNo,
                    amount.intValue(),
                    encodedContent,
                    encodedAccountName);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Lỗi tạo QR code", e);
        }
    }

    /**
     * Lấy session theo ID
     */
    public TtaPaymentSession getSession(Integer sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên thanh toán"));
    }

    /**
     * Kiểm tra trạng thái thanh toán và tạo đơn hàng nếu thành công
     */
    @Transactional
    public Map<String, Object> checkPaymentStatus(Integer sessionId) {
        System.out.println("=== [PAYMENT-CHECK] Starting check for session: " + sessionId);

        TtaPaymentSession session = getSession(sessionId);

        System.out.println("[PAYMENT-CHECK] Session details:");
        System.out.println("  - Amount: " + session.getTtaTongTien());
        System.out.println("  - Content: " + session.getTtaNoiDungCK());
        System.out.println("  - Status: " + session.getTtaTrangThai());
        System.out.println("  - Expired: " + session.isExpired());

        // Kiểm tra đã hết hạn chưa
        if (session.isExpired() && session.isPending()) {
            System.out.println("[PAYMENT-CHECK] Session expired, marking as expired");
            session.markAsExpired();
            sessionRepository.save(session);
            return Map.of(
                    "status", "expired",
                    "message", "Phiên thanh toán đã hết hạn");
        }

        // Nếu đã thanh toán rồi thì trả về luôn
        if (session.isPaid()) {
            System.out.println("[PAYMENT-CHECK] Session already paid, returning order ID: "
                    + session.getTtaDonHang().getTtaMaDonHang());
            return Map.of(
                    "status", "paid",
                    "message", "Đã thanh toán",
                    "orderId", session.getTtaDonHang().getTtaMaDonHang());
        }

        // Gọi Flask API để kiểm tra giao dịch
        try {
            String url = String.format(
                    "%s/api/check-payment?amount=%s&content=%s",
                    vpbankApiUrl,
                    session.getTtaTongTien().intValue(),
                    URLEncoder.encode(session.getTtaNoiDungCK(), StandardCharsets.UTF_8.toString()));

            System.out.println("[PAYMENT-CHECK] Calling Flask API:");
            System.out.println("  URL: " + url);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            System.out.println("[PAYMENT-CHECK] Flask response received:");
            System.out.println("  - Success: " + (response != null ? response.get("success") : "null"));
            System.out.println("  - Found: " + (response != null ? response.get("found") : "null"));

            if (response != null && Boolean.TRUE.equals(response.get("found"))) {
                System.out.println("[PAYMENT-CHECK] ✓ PAYMENT FOUND! Creating order...");

                // Tìm thấy giao dịch - TẠO ĐƠN HÀNG
                @SuppressWarnings("unchecked")
                Map<String, Object> transaction = (Map<String, Object>) response.get("transaction");
                String transactionRef = transaction != null ? (String) transaction.get("reference") : null;

                System.out.println("[PAYMENT-CHECK] Transaction reference: " + transactionRef);

                // Tạo đơn hàng từ session data
                TtaDonHang donHang = cartService.createOrderFromSession(session);
                System.out.println("[PAYMENT-CHECK] Order created with ID: " + donHang.getTtaMaDonHang());

                // Cập nhật session
                session.markAsPaid(transactionRef);
                session.setTtaDonHang(donHang);
                sessionRepository.save(session);

                // Xóa giỏ hàng
                cartService.clearCart();
                System.out.println("[PAYMENT-CHECK] Cart cleared");

                System.out.println("[PAYMENT-CHECK] ✓ Payment processing completed successfully!");

                return Map.of(
                        "status", "paid",
                        "message", "Thanh toán thành công",
                        "orderId", donHang.getTtaMaDonHang(),
                        "transactionRef", transactionRef != null ? transactionRef : "N/A");
            } else {
                System.out.println("[PAYMENT-CHECK] Payment not found yet, status: pending");
                if (response != null && response.get("message") != null) {
                    System.out.println("  Message: " + response.get("message"));
                }
                if (response != null && response.get("error") != null) {
                    System.out.println("  Error: " + response.get("error"));
                }

                return Map.of(
                        "status", "pending",
                        "message", "Đang chờ thanh toán");
            }
        } catch (Exception e) {
            System.out.println("[PAYMENT-CHECK] ✗ ERROR occurred:");
            System.out.println("  Exception: " + e.getClass().getName());
            System.out.println("  Message: " + e.getMessage());
            e.printStackTrace();

            return Map.of(
                    "status", "error",
                    "message", "Lỗi khi kiểm tra thanh toán: " + e.getMessage());
        }
    }

    /**
     * Hủy session
     */
    @Transactional
    public void cancelSession(Integer sessionId) {
        TtaPaymentSession session = getSession(sessionId);

        if (session.isPending()) {
            session.markAsCancelled();
            sessionRepository.save(session);
        }
    }

    /**
     * Tự động hủy các session hết hạn
     */
    @Transactional
    public void expireOldSessions() {
        List<TtaPaymentSession> expiredSessions = sessionRepository
                .findByTtaTrangThaiAndTtaNgayHetHanBefore(
                        TtaTrangThaiSession.PENDING,
                        LocalDateTime.now());

        for (TtaPaymentSession session : expiredSessions) {
            session.markAsExpired();
        }

        if (!expiredSessions.isEmpty()) {
            sessionRepository.saveAll(expiredSessions);
        }
    }
}
