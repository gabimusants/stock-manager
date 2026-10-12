package com.gabrielle.stockmanager.dto;

import java.util.Map;

public record ProductEnrichedResponseDTO(
        ProductResponseDTO product,
        Map<String, Object> catalogAttributes
) {}
