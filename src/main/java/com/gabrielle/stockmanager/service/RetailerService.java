package com.gabrielle.stockmanager.service;

import java.util.List;

import com.gabrielle.stockmanager.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.gabrielle.stockmanager.dto.RetailerRequestDTO;
import com.gabrielle.stockmanager.dto.RetailerResponseDTO;
import com.gabrielle.stockmanager.model.Retailer;
import com.gabrielle.stockmanager.repository.RetailerRepository;
import com.gabrielle.stockmanager.mapper.RetailerMapper;

@Service
public class RetailerService {
    private final RetailerRepository retailerRepository;
    private final RetailerMapper retailerMapper;

    public RetailerService(RetailerRepository retailerRepository, RetailerMapper retailerMapper) {
        this.retailerRepository = retailerRepository;
        this.retailerMapper = retailerMapper;
    }

    public RetailerResponseDTO create(RetailerRequestDTO dto) {
        Retailer retailer = retailerMapper.toEntity(dto);
        Retailer saved = retailerRepository.save(retailer);
        return retailerMapper.toResponseDTO(saved);
    }

    public List<RetailerResponseDTO> findAll() {
        return retailerRepository.findAll()
                .stream()
                .map(retailerMapper::toResponseDTO)
                .toList();
    }

    public RetailerResponseDTO findById(Long id) {
        Retailer retailer = retailerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Retailer not found with id: " + id));
        return retailerMapper.toResponseDTO(retailer);
    }

    public RetailerResponseDTO update(Long id, RetailerRequestDTO dto) {
        Retailer retailer = retailerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Retailer not found with id: " + id));
        retailer.setCompanyName(dto.companyName());
        retailer.setCnpj(dto.cnpj());
        retailer.setEmail(dto.email());
        retailer.setPhone(dto.phone());
        Retailer saved = retailerRepository.save(retailer);
        return retailerMapper.toResponseDTO(saved);
    }

    public void delete(Long id) {
        if (!retailerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Retailer not found with id: " + id);
        }
        retailerRepository.deleteById(id);
    }
}
