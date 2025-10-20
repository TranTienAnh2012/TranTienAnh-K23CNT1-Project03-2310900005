package com.devmaster.lession01_spring_boot.pkg_default_method;

public class MultiInheritance implements Interface1, Interface2 {
    @Override
    public void method1() {
        // Gọi method mặc định trong Interface1
        Interface1.super.method1();
    }

    @Override
    public void method2() {
        // Ghi đè method của Interface2
        System.out.println("MultiInheritance.method2()");
    }

    public static void main(String[] args) {
        MultiInheritance mi = new MultiInheritance();
        mi.method1();
        mi.method2();
    }
}
