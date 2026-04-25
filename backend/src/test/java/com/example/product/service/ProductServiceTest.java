package com.example.product.service;

import com.example.product.entity.Product;
import com.example.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

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
    @DisplayName("getAllProducts - Should return all products from repository")
    void getAllProducts_ShouldReturnAllProducts() {
        List<Product> expectedProducts = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(expectedProducts);

        List<Product> actualProducts = productService.getAllProducts();

        assertThat(actualProducts).hasSize(2);
        assertThat(actualProducts.get(0).getName()).isEqualTo("Wireless Headphones");
        assertThat(actualProducts.get(1).getName()).isEqualTo("Gaming Mouse");
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getProductById - Should return product when found")
    void getProductById_WhenFound_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        Product result = productService.getProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Wireless Headphones");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("99.99"));
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getProductById - Should throw exception when not found")
    void getProductById_WhenNotFound_ShouldThrowException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found with id: 999");

        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("createProduct - Should save and return product")
    void createProduct_ShouldSaveAndReturnProduct() {
        Product newProduct = new Product(null, "Mechanical Keyboard", "RGB mechanical keyboard",
                new BigDecimal("129.99"), 75, "Electronics");
        Product savedProduct = new Product(3L, "Mechanical Keyboard", "RGB mechanical keyboard",
                new BigDecimal("129.99"), 75, "Electronics");

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = productService.createProduct(newProduct);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("Mechanical Keyboard");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("updateProduct - Should update existing product")
    void updateProduct_ShouldUpdateAndReturnProduct() {
        Product existingProduct = new Product(1L, "Old Name", "Old description",
                new BigDecimal("50.00"), 10, "Old Category");
        Product updateRequest = new Product(null, "Updated Headphones", "Updated description",
                new BigDecimal("79.99"), 100, "Audio");
        Product updatedProduct = new Product(1L, "Updated Headphones", "Updated description",
                new BigDecimal("79.99"), 100, "Audio");

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.updateProduct(1L, updateRequest);

        assertThat(result.getName()).isEqualTo("Updated Headphones");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("79.99"));
        assertThat(result.getCategory()).isEqualTo("Audio");
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("deleteProduct - Should delete product when found")
    void deleteProduct_WhenFound_ShouldDeleteProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        doNothing().when(productRepository).delete(any(Product.class));

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(any(Product.class));
    }

    @Test
    @DisplayName("deleteProduct - Should throw exception when not found")
    void deleteProduct_WhenNotFound_ShouldThrowException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found with id: 999");

        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).delete(any(Product.class));
    }
}

