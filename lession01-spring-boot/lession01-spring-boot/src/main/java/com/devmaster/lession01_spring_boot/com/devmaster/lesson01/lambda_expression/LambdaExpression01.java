package com.devmaster.lession01_spring_boot.com.devmaster.lesson01.lambda_expression;

// Tạo functional interface (chỉ có 1 phương thức trừu tượng)
@FunctionalInterface
interface Hello1 {
    void sayHello(); // phương thức trừu tượng
}

// Lớp chính để chạy chương trình
public class LambdaExpression01 {
    public static void main(String[] args) {

        // Sử dụng Lambda Expression để cài đặt phương thức sayHello()
        Hello1 sayHello = () -> {
            System.out.println("Hello World");
        };

        // Gọi phương thức sayHello()
        sayHello.sayHello();
    }
}
