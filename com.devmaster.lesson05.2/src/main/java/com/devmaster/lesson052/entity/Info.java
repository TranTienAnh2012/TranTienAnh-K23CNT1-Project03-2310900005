package com.devmaster.lesson052.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor // tạo constructor đầy đủ tham số
@NoArgsConstructor  // tạo constructor mặc định
public class Info {
    private String name;      // tên tổ chức hoặc người
    private String username;  // nickname hoặc username
    private String email;     // email
    private String website;   // website
}
