package com.gabrielle.stockmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RetailerRequestDTO(
        @NotBlank(message = "Company Name é obrigatório")
        String companyName,

        @NotBlank(message = "CNPJ é obrigatório")
        String cnpj,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email deve ter um formato válido")
        String email,
        String phone
) {}
