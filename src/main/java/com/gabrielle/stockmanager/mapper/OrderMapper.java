package com.gabrielle.stockmanager.mapper;

import com.gabrielle.stockmanager.dto.OrderItemResponseDTO;
import com.gabrielle.stockmanager.dto.OrderResponseDTO;
import com.gabrielle.stockmanager.model.Order;
import com.gabrielle.stockmanager.model.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponseDTO toResponseDTO(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getRetailer().getId(),
                order.getRetailer().getCompanyName(),
                order.getOrderDate(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getItems().stream()
                        .map(this::toItemResponseDTO)
                        .toList()
        );
    }

    private OrderItemResponseDTO toItemResponseDTO(OrderItem item) {
        return new OrderItemResponseDTO(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPriceAtPurchase(),
                item.getUnitPriceAtPurchase().multiply(java.math.BigDecimal.valueOf(item.getQuantity()))
        );
    }
}