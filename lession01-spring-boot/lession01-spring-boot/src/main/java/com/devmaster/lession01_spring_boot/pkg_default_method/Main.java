package com.devmaster.lession01_spring_boot.pkg_default_method;

public class Main {
    public static void main(String[] args) {
        Rectangle rec = new Rectangle();

        rec.draw();           // Gọi method bắt buộc
        rec.setColor("đỏ");   // Gọi default method từ interface
    }
}
