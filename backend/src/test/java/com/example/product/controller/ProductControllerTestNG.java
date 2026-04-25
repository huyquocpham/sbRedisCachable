package com.example.product.controller;

import com.example.product.entity.Product;
import com.example.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProductControllerTestNG {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ObjectMapper objectMapper;

    private Product product1;
    private Product product2;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();

        product1 = new Product(1L, "Wireless Headphones", "Noise cancelling headphones",
                new BigDecimal("99.99"), 150, "Electronics");
        product2 = new Product(2L, "Gaming Mouse", "RGB gaming mouse with 12 buttons",
                new BigDecimal("49.99"), 200, "Electronics");
    }

    @Test(description = "GET /api/products - Should return all products")
    public void testGetAllProducts() throws Exception {
        List<Product> products = Arrays.asList(product1, product2);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Wireless Headphones"))
                .andExpect(jsonPath("$[1].name").value("Gaming Mouse"));

        verify(productService, times(1)).getAllProducts();
    }

    @Test(description = "GET /api/products/{id} - Should return product by id")
    public void testGetProductById() throws Exception {
        when(productService.getProductById(1L)).thenReturn(product1);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Wireless Headphones"))
                .andExpect(jsonPath("$.price").value(99.99));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test(description = "POST /api/products - Should create new product")
    public void testCreateProduct() throws Exception {
        Product newProduct = new Product(null, "Mechanical Keyboard", "RGB mechanical keyboard",
                new BigDecimal("129.99"), 75, "Electronics");
        Product savedProduct = new Product(3L, "Mechanical Keyboard", "RGB mechanical keyboard",
                new BigDecimal("129.99"), 75, "Electronics");

        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Mechanical Keyboard"));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test(description = "PUT /api/products/{id} - Should update existing product")
    public void testUpdateProduct() throws Exception {
        Product updateRequest = new Product(null, "Updated Headphones", "Updated description",
                new BigDecimal("79.99"), 100, "Audio");
        Product updatedProduct = new Product(1L, "Updated Headphones", "Updated description",
                new BigDecimal("79.99"), 100, "Audio");

        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Headphones"))
                .andExpect(jsonPath("$.category").value("Audio"));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test(description = "DELETE /api/products/{id} - Should delete product")
    public void testDeleteProduct() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }
}

