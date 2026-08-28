package com.gabrielle.stockmanager.dto;

public record RetailerResponseDTO(
        Long id,
        String companyName,
        String cnpj,
        String email,
        String phone
) {}
