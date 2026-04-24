package com.example.product.service;

import com.example.product.entity.Product;
import com.example.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "products")
public class ProductService {

    private final ProductRepository productRepository;

    @Cacheable(key = "'allProducts'")
    public List<Product> getAllProducts() {
        log.info("Fetching all products from database...");
        List<Product> products = productRepository.findAll();
        log.debug("Retrieved {} products from database", products.size());
        return products;
    }

    @Cacheable(key = "#id")
    public Product getProductById(Long id) {
        log.info("Fetching product {} from database...", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found with id: {}", id);
                    return new RuntimeException("Product not found with id: " + id);
                });
        log.debug("Retrieved product: {}", product);
        return product;
    }

    @Caching(
        put = @CachePut(key = "#product.id"),
        evict = @CacheEvict(key = "'allProducts'")
    )
    public Product createProduct(Product product) {
        log.info("Creating product: {}", product);
        Product created = productRepository.save(product);
        log.info("Product created successfully with id: {}", created.getId());
        return created;
    }

    @Caching(
        put = @CachePut(key = "#id"),
        evict = @CacheEvict(key = "'allProducts'")
    )
    public Product updateProduct(Long id, Product productDetails) {
        log.info("Updating product {} with details: {}", id, productDetails);
        Product product = getProductById(id);
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStockQuantity(productDetails.getStockQuantity());
        product.setCategory(productDetails.getCategory());
        Product updated = productRepository.save(product);
        log.info("Product {} updated successfully", id);
        return updated;
    }

    @Caching(
        evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(key = "'allProducts'")
        }
    )
    public void deleteProduct(Long id) {
        log.info("Deleting product {} from database...", id);
        Product product = getProductById(id);
        productRepository.delete(product);
        log.info("Product {} deleted successfully", id);
    }
}

