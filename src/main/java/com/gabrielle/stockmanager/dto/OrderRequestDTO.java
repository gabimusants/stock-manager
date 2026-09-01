package com.gabrielle.stockmanager.dto;

import java.util.List;

public record OrderRequestDTO(
        Long retailerId,
        List<OrderItemRequestDTO> items
) {}
