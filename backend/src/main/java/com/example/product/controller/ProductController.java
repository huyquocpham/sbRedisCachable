package com.example.product.controller;

import com.example.product.entity.Product;
import com.example.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Product API", description = "CRUD operations for Product management")
public class ProductController {
   
    private final ProductService productService;

    @Operation(
            summary = "Get all products",
            description = "Retrieves a list of all products. Results are cached in Redis."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved product list",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))
    )
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        log.info("GET /api/products - Retrieving all products");
        List<Product> products = productService.getAllProducts();
        log.info("GET /api/products - Retrieved {} products", products.size());
        return ResponseEntity.ok(products);
    }

    @Operation(
            summary = "Get product by ID",
            description = "Retrieves a single product by its unique identifier. Results are cached in Redis."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable Long id) {
        log.debug("ProductController@getProductById");
        log.info("GET /api/products/{} - Retrieving product by id", id);
        Product product = productService.getProductById(id);
        log.info("GET /api/products/{} - Product retrieved successfully", id);
        return ResponseEntity.ok(product);
    }

    @Operation(
            summary = "Create a new product",
            description = "Creates a new product and stores it in the database. Cache is invalidated."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Product> createProduct(
            @Parameter(description = "Product to create", required = true,
                    content = @Content(schema = @Schema(implementation = Product.class)))
            @RequestBody Product product) {
        log.info("POST /api/products - Creating product: {}", product);
        Product createdProduct = productService.createProduct(product);
        log.info("POST /api/products - Product created with id: {}", createdProduct.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @Operation(
            summary = "Update an existing product",
            description = "Updates a product by ID. Cache is updated and invalidated."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Updated product data", required = true,
                    content = @Content(schema = @Schema(implementation = Product.class)))
            @RequestBody Product product) {
        log.info("PUT /api/products/{} - Updating product: {}", id, product);
        Product updatedProduct = productService.updateProduct(id, product);
        log.info("PUT /api/products/{} - Product updated successfully", id);
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(
            summary = "Delete a product",
            description = "Deletes a product by ID. Cache is invalidated."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/products/{} - Deleting product", id);
        productService.deleteProduct(id);
        log.info("DELETE /api/products/{} - Product deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}

