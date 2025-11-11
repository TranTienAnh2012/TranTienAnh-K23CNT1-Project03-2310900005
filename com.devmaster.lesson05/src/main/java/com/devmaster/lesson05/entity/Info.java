package com.devmaster.lesson05.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor  // Constructor có đầy đủ tham số
@NoArgsConstructor   // Constructor rỗng
public class Info {
    private String name;
    private String username; // trùng với dữ liệu trong controller
    private String email;
    private String website;
}
