package com.gabrielle.stockmanager.service;

import com.gabrielle.stockmanager.dto.ProductRequestDTO;
import com.gabrielle.stockmanager.dto.ProductResponseDTO;
import com.gabrielle.stockmanager.exception.ResourceNotFoundException;
import com.gabrielle.stockmanager.mapper.ProductMapper;
import com.gabrielle.stockmanager.model.Product;
import com.gabrielle.stockmanager.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void findById_deveRetornarProdutoQuandoExiste() {
        // Arrange
        Product product = new Product();
        product.setId(1L);
        product.setSku("SKU-001");
        product.setName("Cerveja Pilsen");
        product.setPrice(new BigDecimal("4.50"));
        product.setStockQuantity(100);

        ProductResponseDTO expectedDto = new ProductResponseDTO(
                1L, "SKU-001", "Cerveja Pilsen", null, new BigDecimal("4.50"), 100
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponseDTO(product)).thenReturn(expectedDto);

        // Act
        ProductResponseDTO result = productService.findById(1L);

        // Assert
        assertThat(result).isEqualTo(expectedDto);
        verify(productRepository).findById(1L);
    }

    @Test
    void findById_deveLancarExcecaoQuandoNaoExiste() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void create_deveSalvarESalvarProdutoCorretamente() {
        ProductRequestDTO requestDto = new ProductRequestDTO(
                "SKU-002", "Refrigerante", "Lata 350ml", new BigDecimal("3.00"), 50
        );

        Product productToSave = new Product();
        Product savedProduct = new Product();
        savedProduct.setId(2L);

        ProductResponseDTO expectedResponse = new ProductResponseDTO(
                2L, "SKU-002", "Refrigerante", "Lata 350ml", new BigDecimal("3.00"), 50
        );

        when(productMapper.toEntity(requestDto)).thenReturn(productToSave);
        when(productRepository.save(productToSave)).thenReturn(savedProduct);
        when(productMapper.toResponseDTO(savedProduct)).thenReturn(expectedResponse);

        ProductResponseDTO result = productService.create(requestDto);

        assertThat(result).isEqualTo(expectedResponse);
        verify(productRepository).save(productToSave);
    }
}