package com.gabrielle.stockmanager.model;

import com.gabrielle.stockmanager.exception.InsufficientStockException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    private Product criarProdutoComEstoque(int estoque) {
        Product product = new Product();
        product.setSku("SKU-TEST");
        product.setName("Produto Teste");
        product.setPrice(new BigDecimal("10.00"));
        product.setStockQuantity(estoque);
        return product;
    }

    @Test
    void reserveStock_deveDecrementarEstoqueQuandoHaSaldo() {
        Product product = criarProdutoComEstoque(10);

        product.reserveStock(4);

        assertThat(product.getStockQuantity()).isEqualTo(6);
    }

    @Test
    void reserveStock_deveLancarExcecaoQuandoEstoqueInsuficiente() {
        Product product = criarProdutoComEstoque(5);

        assertThatThrownBy(() -> product.reserveStock(10))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("Disponível: 5");

        // Importante: o estoque NÃO deve ter sido alterado quando a exceção é lançada
        assertThat(product.getStockQuantity()).isEqualTo(5);
    }

    @Test
    void reserveStock_deveLancarExcecaoQuandoQuantidadeForZeroOuNegativa() {
        Product product = criarProdutoComEstoque(10);

        assertThatThrownBy(() -> product.reserveStock(0))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> product.reserveStock(-5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void reserveStock_devePermitirReservarExatamenteOEstoqueDisponivel() {
        // Caso de borda: reservar exatamente a quantidade disponível deve funcionar
        Product product = criarProdutoComEstoque(5);

        product.reserveStock(5);

        assertThat(product.getStockQuantity()).isEqualTo(0);
    }

    @Test
    void releaseStock_deveIncrementarEstoque() {
        Product product = criarProdutoComEstoque(10);

        product.releaseStock(3);

        assertThat(product.getStockQuantity()).isEqualTo(13);
    }
}