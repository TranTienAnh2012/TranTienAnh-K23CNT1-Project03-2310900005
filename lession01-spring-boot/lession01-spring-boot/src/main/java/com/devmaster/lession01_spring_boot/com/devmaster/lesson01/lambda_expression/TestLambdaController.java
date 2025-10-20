package com.devmaster.lession01_spring_boot.com.devmaster.lesson01.lambda_expression;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Controller Spring Boot
@RestController
public class TestLambdaController {

    // ===== testInterface =====
    interface testInterface {
        void draw();
        default void setColor(String color) {
            System.out.println("Vẽ hình với màu: " + color);
        }
    }

    class Shape implements testInterface {
        @Override
        public void draw() {
            System.out.println("Đang vẽ hình...");
        }
    }

    // ===== MultiInheritance =====
    interface Interface1 {
        default void method1() {
            System.out.println("Interface1.method1()");
        }
    }

    interface Interface2 {
        default void method2() {
            System.out.println("Interface2.method2()");
        }
    }

    class MultiImpl implements Interface1, Interface2 {
        @Override
        public void method1() {
            Interface1.super.method1();
        }

        @Override
        public void method2() {
            System.out.println("MultiImpl.method2()");
        }
    }

    // Endpoint test testInterface
    @GetMapping("/test-shape")
    public String testShape() {
        Shape shape = new Shape();
        shape.draw();
        shape.setColor("Đỏ");
        return "Đã chạy draw() và setColor() – xem log console!";
    }

    // Endpoint test MultiInheritance
    @GetMapping("/test-multi")
    public String testMulti() {
        MultiImpl obj = new MultiImpl();
        obj.method1();
        obj.method2();
        return "Đã chạy method1() và method2() – xem log console!";
    }
}
