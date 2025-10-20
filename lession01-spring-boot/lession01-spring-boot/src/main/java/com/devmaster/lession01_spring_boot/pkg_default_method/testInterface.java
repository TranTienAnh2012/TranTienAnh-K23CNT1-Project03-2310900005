package com.devmaster.lession01_spring_boot.pkg_default_method;

public interface testInterface {
    void draw();

    default void setColor(String color) {
        System.out.println("Ve hinh voi mau: " + color);
    }

}
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
