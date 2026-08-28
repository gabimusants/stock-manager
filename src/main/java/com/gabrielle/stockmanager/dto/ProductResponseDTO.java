package com.gabrielle.stockmanager.dto;

import java.math.BigDecimal;

public record ProductResponseDTO(
        Long id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity
) {}
