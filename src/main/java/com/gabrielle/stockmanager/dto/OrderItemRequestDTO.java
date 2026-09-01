package com.gabrielle.stockmanager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequestDTO(
        @NotNull(message = "Id do produto é obrigatório")
        Long productId,

        @NotNull(message = "Quantidade é obrigatória")
        @Min(value = 1, message = "Quantidade deve ser ao menos 1")
        Integer quantity
) {}