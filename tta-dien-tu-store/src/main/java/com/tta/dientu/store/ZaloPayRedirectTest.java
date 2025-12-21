package com.tta.dientu.store;

import com.sun.net.httpserver.HttpServer;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

public class ZaloPayRedirectTest {

    // ===== ZALOPAY SANDBOX =====
    private static final String APP_ID = "2554";
    private static final String KEY1 = "sdngKKJmqEMzvh5QQcdD2A9XBSKUNaYn";
    private static final String ENDPOINT = "https://sb-openapi.zalopay.vn/v2/create";

    public static void main(String[] args) throws Exception {

        // ===== 1. ORDER INFO =====
        String appTransId = new java.text.SimpleDateFormat("yyMMdd").format(new Date())
                + "_" + System.currentTimeMillis();

        Map<String, Object> order = new LinkedHashMap<>();
        order.put("app_id", APP_ID);
        order.put("app_trans_id", appTransId);
        order.put("app_user", "demo_user");
        order.put("app_time", System.currentTimeMillis());
        order.put("amount", 10000);
        order.put("item", "[]");
        order.put("description", "Test thanh toán ZaloPay");
        order.put("embed_data", "{}");
        order.put("callback_url", "http://localhost:9001/callback");

        // ===== 2. CREATE MAC =====
        String data = order.get("app_id") + "|"
                + order.get("app_trans_id") + "|"
                + order.get("app_user") + "|"
                + order.get("amount") + "|"
                + order.get("app_time") + "|"
                + order.get("embed_data") + "|"
                + order.get("item");

        String mac = hmacSHA256(data, KEY1);
        order.put("mac", mac);

        // ===== 3. CALL ZALOPAY =====
        String form = buildForm(order);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        String body = response.body();
        System.out.println("ZALOPAY RESPONSE:");
        System.out.println(body);

        if (!body.contains("\"order_url\"")) {
            System.out.println("❌ Không lấy được order_url");
            return;
        }

        String orderUrl = body.split("\"order_url\":\"")[1].split("\"")[0];

        // ===== 4. MINI SERVER → REDIRECT =====
        HttpServer server = HttpServer.create(new InetSocketAddress(9001), 0);

        server.createContext("/", exchange -> {
            exchange.getResponseHeaders().add("Location", orderUrl);
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
        });

        server.createContext("/callback", exchange -> {
            String html = """
            <html>
            <body style="text-align:center;font-family:Arial">
                <h1 style="color:green">✅ ZALOPAY CALLBACK</h1>
                <p>Thanh toán hoàn tất (sandbox)</p>
            </body>
            </html>
            """;
            exchange.sendResponseHeaders(200, html.getBytes().length);
            exchange.getResponseBody().write(html.getBytes());
            exchange.close();
        });

        server.start();

        System.out.println("================================");
        System.out.println("🌐 MỞ TRÌNH DUYỆT:");
        System.out.println("👉 http://localhost:9001");
        System.out.println("➡️ SẼ REDIRECT SANG GIAO DIỆN ZALOPAY");
        System.out.println("================================");
    }

    // ===== HMAC SHA256 =====
    private static String hmacSHA256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : raw) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    // ===== FORM ENCODE =====
    private static String buildForm(Map<String, Object> map) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> e : map.entrySet()) {
            sb.append(URLEncoder.encode(e.getKey(), "UTF-8"))
                    .append("=")
                    .append(URLEncoder.encode(String.valueOf(e.getValue()), "UTF-8"))
                    .append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
