package com.gabrielle.stockmanager.dto;

import com.gabrielle.stockmanager.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        Long id,
        Long retailerId,
        String retailerCompanyName,
        LocalDateTime orderDate,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderItemResponseDTO> items
) {}