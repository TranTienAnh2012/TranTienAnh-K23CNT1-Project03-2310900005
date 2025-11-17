package com.tta.devmaster.lesson07.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "Product")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductID")
    Integer id;

    @Column(name = "ProductName", nullable = false)
    String name;

    @Column(name = "ImageUrl")
    String imageUrl;

    @Column(name = "Quantity")
    Integer quantity;

    @Column(name = "Price")
    Double price;

    @Column(name = "Content")
    String content;

    @Column(name = "Description")
    String description;

    @Column(name = "CreatedAt", insertable = false, updatable = false)
    java.sql.Timestamp createdAt;

    @Column(name = "Status")
    Boolean status;

    // ManyToOne mapping với Category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CategoryID", nullable = false)
    @ToString.Exclude   // tránh lỗi stack overflow
            TtaCategory category;
}
