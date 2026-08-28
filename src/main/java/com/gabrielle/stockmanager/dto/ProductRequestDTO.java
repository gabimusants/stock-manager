package com.gabrielle.stockmanager.dto;

import java.math.BigDecimal;

public record ProductRequestDTO(
        String sku,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity
) {}
