package com.gabrielle.stockmanager.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    void addItem_deveSincronizarOsDoisLadosDaRelacao() {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        OrderItem item = new OrderItem();
        item.setQuantity(5);
        item.setUnitPriceAtPurchase(new BigDecimal("10.00"));

        order.addItem(item);

        assertThat(order.getItems()).contains(item);
        assertThat(item.getOrder()).isEqualTo(order);
    }

    @Test
    void removeItem_deveDessincronizarOsDoisLadosDaRelacao() {
        Order order = new Order();
        OrderItem item = new OrderItem();
        order.addItem(item);

        order.removeItem(item);

        assertThat(order.getItems()).doesNotContain(item);
        assertThat(item.getOrder()).isNull();
    }

    @Test
    void toString_naoDeveLancarExcecaoNemEntrarEmLoop() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        OrderItem item = new OrderItem();
        item.setId(1L);
        order.addItem(item);

        String orderAsString = order.toString();
        String itemAsString = item.toString();

        assertThat(orderAsString).isNotNull();
        assertThat(itemAsString).isNotNull();
    }
}
