package com.example.product.controller;

import com.example.product.entity.Product;
import com.example.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {
   
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        log.info("GET /api/products - Retrieving all products");
        List<Product> products = productService.getAllProducts();
        log.info("GET /api/products - Retrieved {} products", products.size());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        log.debug("ProductController@getProductById");
        log.info("GET /api/products/{} - Retrieving product by id", id);
        Product product = productService.getProductById(id);
        log.info("GET /api/products/{} - Product retrieved successfully", id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        log.info("POST /api/products - Creating product: {}", product);
        Product createdProduct = productService.createProduct(product);
        log.info("POST /api/products - Product created with id: {}", createdProduct.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        log.info("PUT /api/products/{} - Updating product: {}", id, product);
        Product updatedProduct = productService.updateProduct(id, product);
        log.info("PUT /api/products/{} - Product updated successfully", id);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("DELETE /api/products/{} - Deleting product", id);
        productService.deleteProduct(id);
        log.info("DELETE /api/products/{} - Product deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}

