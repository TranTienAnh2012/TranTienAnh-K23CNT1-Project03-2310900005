package com.devmaster.trantienanh.lesson04.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String username;

    String password;

    String fullName;

    LocalDate birthday; // chú ý chữ D viết hoa

    String email;

    String phone;

    int age;

    Boolean status;
}
