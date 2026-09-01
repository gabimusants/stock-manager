package com.gabrielle.stockmanager.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequestDTO(
        @NotBlank(message = "SKU é obrigatório")
        String sku,

        @NotBlank(message = "Nome é obrigatório")
        String name,

        String description,

        @NotNull(message = "Preço é obrigatório")
        @DecimalMin(value = "0.0", inclusive = false, message = "Preço deve ser maior que zero")
        BigDecimal price,

        @NotNull(message = "Quantidade em estoque é obrigatória")
        @Min(value = 0, message = "Quantidade em estoque não pode ser negativa")
        Integer stockQuantity
) {}
