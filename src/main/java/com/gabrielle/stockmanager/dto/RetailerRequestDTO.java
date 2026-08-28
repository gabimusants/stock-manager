package com.gabrielle.stockmanager.dto;

public record RetailerRequestDTO(
        String companyName,
        String cnpj,
        String email,
        String phone
) {}
