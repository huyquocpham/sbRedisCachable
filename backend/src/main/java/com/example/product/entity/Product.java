package com.example.product.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;

@Slf4j
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product entity representing an item in the catalog")
public class Product implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique product identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    @Column(nullable = false)
    @Schema(description = "Product name", example = "Wireless Headphones", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    
    @Column(length = 1000)
    @Schema(description = "Product description", example = "High-quality noise cancelling wireless headphones with 20h battery life")
    private String description;
    
    @Column(nullable = false)
    @Schema(description = "Product price", example = "99.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal price;
    
    @Column(name = "stock_quantity")
    @Schema(description = "Available stock quantity", example = "150")
    private Integer stockQuantity;
    
    @Column(name = "category")
    @Schema(description = "Product category", example = "Electronics")
    private String category;
    
    public void logProductInfo() {
        log.info("Product[id={}, name={}, category={}, price={}]", id, name, category, price);
    }
}

