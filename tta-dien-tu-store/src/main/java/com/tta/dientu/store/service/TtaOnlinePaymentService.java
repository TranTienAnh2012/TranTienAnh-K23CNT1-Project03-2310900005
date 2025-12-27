package com.tta.dientu.store.service;

import com.tta.dientu.store.entity.TtaDonHang;
import com.tta.dientu.store.entity.TtaOnlinePayment;
import com.tta.dientu.store.enums.TtaPhuongThucThanhToan;
import com.tta.dientu.store.enums.TtaTrangThaiThanhToan;
import com.tta.dientu.store.enums.TtaTrangThaiThanhToanOnline;
import com.tta.dientu.store.repository.TtaDonHangRepository;
import com.tta.dientu.store.repository.TtaOnlinePaymentRepository;
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
@RequiredArgsConstructor
public class TtaOnlinePaymentService {

    private final TtaOnlinePaymentRepository onlinePaymentRepository;
    private final TtaDonHangRepository donHangRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${vpbank.api.url:http://localhost:5000}")
    private String vpbankApiUrl;

    @Value("${vpbank.account.number:0387742492}")
    private String accountNumber;

    @Value("${vpbank.account.name:TRAN TIEN ANH}")
    private String accountName;

    @Value("${vpbank.bank.code:VPB}")
    private String bankCode;

    @Value("${payment.timeout.minutes:15}")
    private int timeoutMinutes;

    /**
     * Tạo yêu cầu thanh toán online mới
     */
    @Transactional
    public TtaOnlinePayment createPayment(Integer orderId, BigDecimal amount) {
        TtaDonHang donHang = donHangRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // If amount not provided, use order total
        if (amount == null) {
            amount = donHang.getTtaTongTien();
        }

        // Kiểm tra xem đơn hàng đã có thanh toán online chưa
        onlinePaymentRepository.findByTtaDonHang_TtaMaDonHang(orderId)
                .ifPresent(payment -> {
                    if (payment.isPending() && !payment.isExpired()) {
                        throw new RuntimeException("Đơn hàng đã có yêu cầu thanh toán đang chờ xử lý");
                    }
                });

        TtaOnlinePayment payment = new TtaOnlinePayment();
        payment.setTtaDonHang(donHang);
        payment.setTtaSoTien(amount);

        // Tạo nội dung chuyển khoản: TTA + mã đơn hàng
        String content = "TTA" + orderId;
        payment.setTtaNoiDung(content);

        // Tạo QR code URL
        String qrUrl = generateQRCodeUrl(accountNumber, amount, content);
        payment.setTtaQrCodeUrl(qrUrl);

        return onlinePaymentRepository.save(payment);
    }

    /**
     * Tạo URL QR code từ VietQR
     */
    public String generateQRCodeUrl(String accountNo, BigDecimal amount, String content) {
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
     * Kiểm tra trạng thái thanh toán qua Flask API
     */
    @Transactional
    public Map<String, Object> checkPaymentStatus(Integer paymentId) {
        TtaOnlinePayment payment = onlinePaymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin thanh toán"));

        // Kiểm tra đã hết hạn chưa
        if (payment.isExpired() && payment.isPending()) {
            payment.markAsExpired();
            onlinePaymentRepository.save(payment);
            return Map.of(
                    "status", "expired",
                    "message", "Thanh toán đã hết hạn");
        }

        // Nếu đã hoàn thành rồi thì trả về luôn
        if (payment.isCompleted()) {
            return Map.of(
                    "status", "completed",
                    "message", "Thanh toán đã hoàn thành",
                    "orderId", payment.getTtaDonHang().getTtaMaDonHang());
        }

        // Gọi Flask API để kiểm tra giao dịch
        try {
            String url = String.format(
                    "%s/api/check-payment?amount=%s&content=%s",
                    vpbankApiUrl,
                    payment.getTtaSoTien().intValue(),
                    URLEncoder.encode(payment.getTtaNoiDung(), StandardCharsets.UTF_8.toString()));

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && Boolean.TRUE.equals(response.get("found"))) {
                // Tìm thấy giao dịch - cập nhật trạng thái
                @SuppressWarnings("unchecked")
                Map<String, Object> transaction = (Map<String, Object>) response.get("transaction");
                String transactionRef = transaction != null ? (String) transaction.get("reference") : null;

                payment.markAsCompleted(transactionRef);
                onlinePaymentRepository.save(payment);

                // Cập nhật trạng thái đơn hàng
                TtaDonHang donHang = payment.getTtaDonHang();
                donHang.setTtaTrangThaiThanhToan(TtaTrangThaiThanhToan.PAID);
                donHangRepository.save(donHang);

                return Map.of(
                        "status", "completed",
                        "message", "Thanh toán thành công",
                        "orderId", donHang.getTtaMaDonHang(),
                        "transactionRef", transactionRef != null ? transactionRef : "N/A");
            } else {
                return Map.of(
                        "status", "pending",
                        "message", "Đang chờ thanh toán");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of(
                    "status", "error",
                    "message", "Lỗi khi kiểm tra thanh toán: " + e.getMessage());
        }
    }

    /**
     * Lấy thông tin thanh toán theo đơn hàng
     */
    public TtaOnlinePayment getPaymentByOrderId(Integer orderId) {
        return onlinePaymentRepository.findByTtaDonHang_TtaMaDonHang(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin thanh toán"));
    }

    /**
     * Hủy các thanh toán đã hết hạn và hủy đơn hàng tương ứng
     */
    @Transactional
    public void expireOldPayments() {
        List<TtaOnlinePayment> expiredPayments = onlinePaymentRepository
                .findByTtaTrangThaiAndTtaNgayHetHanBefore(
                        TtaTrangThaiThanhToanOnline.PENDING,
                        LocalDateTime.now());

        for (TtaOnlinePayment payment : expiredPayments) {
            payment.markAsExpired();

            // Hủy đơn hàng nếu chưa thanh toán
            TtaDonHang donHang = payment.getTtaDonHang();
            if (donHang.getTtaTrangThaiThanhToan() == TtaTrangThaiThanhToan.UNPAID) {
                donHang.setTtaTrangThai(com.tta.dientu.store.enums.TtaTrangThaiDonHang.DA_HUY);
                donHangRepository.save(donHang);
            }
        }

        if (!expiredPayments.isEmpty()) {
            onlinePaymentRepository.saveAll(expiredPayments);
        }
    }

    /**
     * Hủy thanh toán và đơn hàng
     */
    @Transactional
    public void cancelPayment(Integer paymentId) {
        TtaOnlinePayment payment = onlinePaymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin thanh toán"));

        if (payment.isPending()) {
            payment.setTtaTrangThai(TtaTrangThaiThanhToanOnline.CANCELLED);
            payment.setTtaNgayCapNhat(LocalDateTime.now());
            onlinePaymentRepository.save(payment);

            // Hủy đơn hàng
            TtaDonHang donHang = payment.getTtaDonHang();
            if (donHang.getTtaTrangThaiThanhToan() == TtaTrangThaiThanhToan.UNPAID) {
                donHang.setTtaTrangThai(com.tta.dientu.store.enums.TtaTrangThaiDonHang.DA_HUY);
                donHangRepository.save(donHang);
            }
        }
    }
}
