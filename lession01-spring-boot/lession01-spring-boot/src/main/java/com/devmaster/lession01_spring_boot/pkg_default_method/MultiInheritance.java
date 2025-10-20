package com.devmaster.lession01_spring_boot.pkg_default_method;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MultiInheritance { // lớp public chính

    // Lớp nội bộ implement cả 2 interface
    class MultiImpl implements Interface1, Interface2 {
        @Override
        public void method1() {
            Interface1.super.method1(); // gọi default method của Interface1
        }

        @Override
        public void method2() {
            System.out.println("MultiImpl.method2()");
        }
    }

    @GetMapping("/test-multi-old")
    public String testMulti() {
        MultiImpl obj = new MultiImpl();
        obj.method1();
        obj.method2();
        return "Đã chạy method1() và method2() – xem log console!";
    }
}
