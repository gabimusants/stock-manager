package com.gabrielle.stockmanager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;

import com.gabrielle.stockmanager.dto.RetailerRequestDTO;
import com.gabrielle.stockmanager.dto.RetailerResponseDTO;
import com.gabrielle.stockmanager.service.RetailerService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/api/retailers")
public class RetailerController {
    private final RetailerService retailerService;

    public RetailerController(RetailerService retailerService) {
        this.retailerService = retailerService;
    }

    @PostMapping
    public ResponseEntity<RetailerResponseDTO> create(@RequestBody RetailerRequestDTO dto) {
        RetailerResponseDTO created = retailerService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<RetailerResponseDTO>> findAll() {
        return ResponseEntity.ok(retailerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RetailerResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(retailerService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RetailerResponseDTO> update(@PathVariable Long id, @RequestBody RetailerRequestDTO dto) {
        return ResponseEntity.ok(retailerService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        retailerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
