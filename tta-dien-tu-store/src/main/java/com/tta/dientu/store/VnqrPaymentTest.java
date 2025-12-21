package com.tta.dientu.store;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class VnqrPaymentTest {

    public static void main(String[] args) throws Exception {

        // ===== 1. THÔNG TIN THANH TOÁN =====
        String bankCode = "VCB";              // Vietcombank
        String accountNo = "0123456789";      // SỐ TÀI KHOẢN TEST
        String accountName = "NGUYEN VAN A";  // TÊN CHỦ TK
        String amount = "10000";
        String orderId = UUID.randomUUID().toString();

        // ===== 2. TẠO NỘI DUNG CHUYỂN KHOẢN =====
        String addInfo = "Thanh toan don hang " + orderId;

        // ===== 3. LINK VNQR (chuẩn VietQR) =====
        String vnqrUrl =
                "https://img.vietqr.io/image/"
                        + bankCode + "-"
                        + accountNo
                        + "-qr_only.png"
                        + "?amount=" + amount
                        + "&addInfo=" + URLEncoder.encode(addInfo, StandardCharsets.UTF_8)
                        + "&accountName=" + URLEncoder.encode(accountName, StandardCharsets.UTF_8);

        // ===== 4. MINI WEB SERVER =====
        HttpServer server = HttpServer.create(new InetSocketAddress(9000), 0);

        // Trang thanh toán
        server.createContext("/", exchange -> {
            String html = """
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Thanh toán VNQR</title>
            </head>
            <body style="text-align:center;font-family:Arial">
                <h2>Thanh toán bằng VNQR</h2>
                <img src="%s" width="300"/><br><br>

                <p><b>Số tiền:</b> %s VND</p>
                <p><b>Nội dung:</b> %s</p>

                <button onclick="location.href='/success'"
                        style="font-size:18px;padding:10px 20px">
                    ✅ Tôi đã thanh toán
                </button>
            </body>
            </html>
            """.formatted(vnqrUrl, amount, addInfo);

            exchange.sendResponseHeaders(200, html.getBytes().length);
            exchange.getResponseBody().write(html.getBytes());
            exchange.close();
        });

        // Trang thành công (MOCK)
        server.createContext("/success", exchange -> {
            String html = """
            <html>
            <body style="text-align:center;font-family:Arial">
                <h1 style="color:green">✅ THANH TOÁN THÀNH CÔNG (MOCK)</h1>
                <p>OrderId: %s</p>
                <p>Số tiền: %s VND</p>
            </body>
            </html>
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
}
