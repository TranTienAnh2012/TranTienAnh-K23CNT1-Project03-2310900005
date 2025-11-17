package com.tta.devmaster.lesson07.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "Category")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TtaCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CategoryID")
    Integer id;

    @Column(name = "CategoryName", nullable = false)
    String categoryName;

    // Mapping với cột CategoryStatus (MySQL là TINYINT(1))
    @Column(name = "CategoryStatus")
    Boolean categoryStatus;

    // OneToMany mapping với Product
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Product> products;
}
