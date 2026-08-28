package com.gabrielle.stockmanager.repository;

import com.gabrielle.stockmanager.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void findBySku_deveEncontrarProdutoSalvo() {
        Product product = new Product();
        product.setSku("SKU-001");
        product.setName("Cerveja Pilsen 350ml");
        product.setPrice(new BigDecimal("4.50"));
        product.setStockQuantity(100);

        productRepository.save(product);

        Optional<Product> encontrado = productRepository.findBySku("SKU-001");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getName()).isEqualTo("Cerveja Pilsen 350ml");
    }

    @Test
    void findBySku_deveRetornarVazioQuandoNaoExiste() {
        Optional<Product> encontrado = productRepository.findBySku("SKU-INEXISTENTE");

        assertThat(encontrado).isEmpty();
    }

    @Test
    void existsBySku_deveRetornarTrueQuandoExiste() {
        Product product = new Product();
        product.setSku("SKU-002");
        product.setName("Refrigerante Lata 350ml");
        product.setPrice(new BigDecimal("3.00"));
        product.setStockQuantity(50);
        productRepository.save(product);

        boolean existe = productRepository.existsBySku("SKU-002");

        assertThat(existe).isTrue();
    }
}
