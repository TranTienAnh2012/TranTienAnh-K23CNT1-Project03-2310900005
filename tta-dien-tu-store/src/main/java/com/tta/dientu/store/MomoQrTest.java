package com.tta.dientu.store;

import com.sun.net.httpserver.HttpServer;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.OutputStream;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MomoQrTest {

    private static final String PARTNER_CODE = "MOMO2LG820251211_TEST";
    private static final String ACCESS_KEY = "3aiJ0IHArdTfw39n";
    private static final String SECRET_KEY = "1ArSVa5rserjFUZ6JwlIBn9WNkVqwO1g";
    private static final String ENDPOINT = "https://test-payment.momo.vn/v2/gateway/api/create";

    public static void main(String[] args) throws Exception {

        // ===== 1. THÔNG TIN ĐƠN HÀNG =====
        String orderId = UUID.randomUUID().toString();
        String requestId = UUID.randomUUID().toString();
        String amount = "10000";
        String orderInfo = "Test thanh toan MoMo QR";
        String redirectUrl = "http://localhost:9000/success";
        String ipnUrl = "http://localhost:9000/ipn";
        String requestType = "captureWallet";
        String extraData = ""; // ✅ BẮT BUỘC

        // ===== 2. RAW SIGNATURE =====
        String rawSignature =
                "accessKey=" + ACCESS_KEY +
                        "&amount=" + amount +
                        "&extraData=" + extraData +
                        "&ipnUrl=" + ipnUrl +
                        "&orderId=" + orderId +
                        "&orderInfo=" + orderInfo +
                        "&partnerCode=" + PARTNER_CODE +
                        "&redirectUrl=" + redirectUrl +
                        "&requestId=" + requestId +
                        "&requestType=" + requestType;

        String signature = hmacSHA256(rawSignature, SECRET_KEY);

        // ===== 3. JSON BODY (PHẢI CÓ extraData) =====
        String jsonBody = """
        {
          "partnerCode":"%s",
          "accessKey":"%s",
          "requestId":"%s",
          "amount":"%s",
          "orderId":"%s",
          "orderInfo":"%s",
          "redirectUrl":"%s",
          "ipnUrl":"%s",
          "extraData":"%s",
          "requestType":"%s",
          "signature":"%s",
          "lang":"vi"
        }
        """.formatted(
                PARTNER_CODE,
                ACCESS_KEY,
                requestId,
                amount,
                orderId,
                orderInfo,
                redirectUrl,
                ipnUrl,
                extraData,
                requestType,
                signature
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response =
                client.send(
                        HttpRequest.newBuilder()
                                .uri(URI.create(ENDPOINT))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                                .build(),
                        HttpResponse.BodyHandlers.ofString()
                );

        String body = response.body();

        if (!body.contains("\"payUrl\"")) {
            System.out.println("❌ Không lấy được payUrl");
            System.out.println(body);
            return;
        }

        String payUrl = body.split("\"payUrl\":\"")[1].split("\"")[0];

        String qrImage =
                "https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=" +
                        URLEncoder.encode(payUrl, StandardCharsets.UTF_8);

        // ===== 4. MINI WEB SERVER =====
        HttpServer server = HttpServer.create(new InetSocketAddress(9000), 0);

        server.createContext("/", exchange -> {
            String html = """
            <html>
            <head><meta charset="UTF-8"><title>Thanh toán MoMo</title></head>
            <body style="text-align:center;font-family:Arial">
                <h2>Quét QR MoMo để thanh toán</h2>
                <img src="%s"/><br><br>
                <button onclick="location.href='/success'"
                        style="font-size:18px;padding:10px 20px">
                    ✅ Xác nhận thanh toán
                </button>
            </body>
            </html>
            """.formatted(qrImage);

            exchange.sendResponseHeaders(200, html.getBytes().length);
            exchange.getResponseBody().write(html.getBytes());
            exchange.close();
        });

        server.createContext("/success", exchange -> {
            String html = """
            <html><body style="text-align:center;font-family:Arial">
                <h1 style="color:green">✅ THANH TOÁN THÀNH CÔNG (MOCK)</h1>
                <p>OrderId: %s</p>
                <p>Số tiền: %s VND</p>
            </body></html>
            """.formatted(orderId, amount);

            exchange.sendResponseHeaders(200, html.getBytes().length);
            exchange.getResponseBody().write(html.getBytes());
            exchange.close();
        });

        server.start();

        System.out.println("================================");
        System.out.println("🌐 MỞ TRÌNH DUYỆT:");
        System.out.println("👉 http://localhost:9000");
        System.out.println("================================");
    }

    // ===== HMAC =====
    private static String hmacSHA256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : raw) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
