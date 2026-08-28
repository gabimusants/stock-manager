package com.gabrielle.stockmanager.mapper;

import com.gabrielle.stockmanager.dto.RetailerRequestDTO;
import com.gabrielle.stockmanager.dto.RetailerResponseDTO;
import com.gabrielle.stockmanager.model.Retailer;
import org.springframework.stereotype.Component;

@Component
public class RetailerMapper {

    public Retailer toEntity(RetailerRequestDTO dto) {
        Retailer retailer = new Retailer();
        retailer.setCompanyName(dto.companyName());
        retailer.setCnpj(dto.cnpj());
        retailer.setEmail(dto.email());
        retailer.setPhone(dto.phone());
        return retailer;
    }

    public RetailerResponseDTO toResponseDTO(Retailer retailer) {
        return new RetailerResponseDTO(
                retailer.getId(),
                retailer.getCompanyName(),
                retailer.getCnpj(),
                retailer.getEmail(),
                retailer.getPhone()
        );
    }
}
