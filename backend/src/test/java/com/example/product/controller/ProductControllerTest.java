package com.example.product.controller;

import com.example.product.entity.Product;
import com.example.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("Product Controller Tests - Spring Test")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Wireless Headphones", "Noise cancelling headphones", 
                new BigDecimal("99.99"), 150, "Electronics");
        product2 = new Product(2L, "Gaming Mouse", "RGB gaming mouse with 12 buttons", 
                new BigDecimal("49.99"), 200, "Electronics");
    }

    @Test
    @DisplayName("GET /api/products - Should return all products")
    void getAllProducts_ShouldReturnProductList() throws Exception {
        List<Product> products = Arrays.asList(product1, product2);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Wireless Headphones"))
                .andExpect(jsonPath("$[0].price").value(99.99))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Gaming Mouse"));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should return product by id")
    void getProductById_ShouldReturnProduct() throws Exception {
        when(productService.getProductById(1L)).thenReturn(product1);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Wireless Headphones"))
                .andExpect(jsonPath("$.description").value("Noise cancelling headphones"))
                .andExpect(jsonPath("$.price").value(99.99))
                .andExpect(jsonPath("$.stockQuantity").value(150))
                .andExpect(jsonPath("$.category").value("Electronics"));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    @DisplayName("POST /api/products - Should create new product")
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        Product newProduct = new Product(null, "Mechanical Keyboard", "RGB mechanical keyboard",
                new BigDecimal("129.99"), 75, "Electronics");
        Product savedProduct = new Product(3L, "Mechanical Keyboard", "RGB mechanical keyboard",
                new BigDecimal("129.99"), 75, "Electronics");

        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Mechanical Keyboard"))
                .andExpect(jsonPath("$.price").value(129.99));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("PUT /api/products/{id} - Should update existing product")
    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
        Product updateRequest = new Product(null, "Updated Headphones", "Updated description",
                new BigDecimal("79.99"), 100, "Audio");
        Product updatedProduct = new Product(1L, "Updated Headphones", "Updated description",
                new BigDecimal("79.99"), 100, "Audio");

        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Headphones"))
                .andExpect(jsonPath("$.price").value(79.99))
                .andExpect(jsonPath("$.category").value("Audio"));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - Should delete product")
    void deleteProduct_ShouldReturnNoContent() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should handle product not found")
    void getProductById_WhenNotFound_ShouldThrowException() throws Exception {
        when(productService.getProductById(999L))
                .thenThrow(new RuntimeException("Product not found with id: 999"));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isInternalServerError());

        verify(productService, times(1)).getProductById(999L);
    }
}

