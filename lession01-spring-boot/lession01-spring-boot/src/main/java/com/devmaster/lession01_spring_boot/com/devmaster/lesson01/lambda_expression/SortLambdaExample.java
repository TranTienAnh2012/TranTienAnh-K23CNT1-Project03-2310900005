package com.devmaster.lession01_spring_boot.com.devmaster.lesson01.lambda_expression;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

public class SortLambdaExample {
    public static void main(String[] args) {
        // Bước 4: Tạo danh sách chuỗi
        List<String> list = Arrays.asList(
                "Java SpringBoot",
                "C# NetCore",
                "PHP",
                "JavaScript"
        );

        // Bước 5: Sắp xếp bằng biểu thức lambda
        Collections.sort(list, (String str1, String str2) -> str1.compareTo(str2));

        // Bước 6: In danh sách sau khi sắp xếp
        for (String str : list) {
            System.out.println(str);
        }
    }
}

