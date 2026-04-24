package com.example.product.repository;

import com.example.product.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    @Slf4j
    class ProductRepositoryLogger {
        public static void logRepositoryAccess(String method) {
            log.debug("ProductRepository method invoked: {}", method);
        }
    }
}

