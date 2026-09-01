package com.gabrielle.stockmanager.dto;

public record OrderItemRequestDTO(
        Long productId,
        Integer quantity
) {}