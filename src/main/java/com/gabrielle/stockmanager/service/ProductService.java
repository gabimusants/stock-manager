package com.gabrielle.stockmanager.service;

import com.gabrielle.stockmanager.dto.ProductRequestDTO;
import com.gabrielle.stockmanager.dto.ProductResponseDTO;
import com.gabrielle.stockmanager.exception.ResourceNotFoundException;
import com.gabrielle.stockmanager.mapper.ProductMapper;
import com.gabrielle.stockmanager.model.Product;
import com.gabrielle.stockmanager.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public ProductResponseDTO create(ProductRequestDTO dto) {
        Product product = productMapper.toEntity(dto);
        Product saved = productRepository.save(product);
        return productMapper.toResponseDTO(saved);
    }

    public List<ProductResponseDTO> findAll() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponseDTO)
                .toList();
    }

    public ProductResponseDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
        return productMapper.toResponseDTO(product);
    }

    public ProductResponseDTO update(Long id, ProductRequestDTO dto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));

        existing.setSku(dto.sku());
        existing.setName(dto.name());
        existing.setDescription(dto.description());
        existing.setPrice(dto.price());
        existing.setStockQuantity(dto.stockQuantity());

        Product updated = productRepository.save(existing);
        return productMapper.toResponseDTO(updated);
    }

    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produto não encontrado: " + id);
        }
        productRepository.deleteById(id);
    }
}
