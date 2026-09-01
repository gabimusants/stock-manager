package com.gabrielle.stockmanager.dto;

import java.math.BigDecimal;

public record OrderItemResponseDTO(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPriceAtPurchase,
        BigDecimal subtotal
) {}