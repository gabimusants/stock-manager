package com.gabrielle.stockmanager.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequestDTO(
        @NotNull(message = "Id do retailer é obrigatório")
        Long retailerId,

        @NotEmpty(message = "O pedido precisa ter ao menos um item")
        @Valid
        List<OrderItemRequestDTO> items
) {}
