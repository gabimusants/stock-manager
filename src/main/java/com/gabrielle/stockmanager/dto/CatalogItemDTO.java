package com.gabrielle.stockmanager.dto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.List;

public record CatalogItemDTO(
        String id,
        String sku,
        String name,
        String category,
        BigDecimal referencePrice,
        Map<String, Object> attributes
) {}
