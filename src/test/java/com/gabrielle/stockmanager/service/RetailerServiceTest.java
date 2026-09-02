package com.gabrielle.stockmanager.service;

import com.gabrielle.stockmanager.dto.RetailerRequestDTO;
import com.gabrielle.stockmanager.dto.RetailerResponseDTO;
import com.gabrielle.stockmanager.exception.ResourceNotFoundException;
import com.gabrielle.stockmanager.mapper.RetailerMapper;
import com.gabrielle.stockmanager.model.Retailer;
import com.gabrielle.stockmanager.repository.RetailerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetailerServiceTest {

    @Mock
    private RetailerRepository retailerRepository;

    @Mock
    private RetailerMapper retailerMapper;

    @InjectMocks
    private RetailerService retailerService;

    @Test
    void findById_deveRetornarRetailerQuandoExiste() {
        Retailer retailer = new Retailer();
        retailer.setId(1L);
        retailer.setCompanyName("Bar do Zé");
        retailer.setCnpj("12345678000199");

        RetailerResponseDTO expectedDto = new RetailerResponseDTO(
                1L, "Bar do Zé", "12345678000199", "contato@bardoze.com", null
        );

        when(retailerRepository.findById(1L)).thenReturn(Optional.of(retailer));
        when(retailerMapper.toResponseDTO(retailer)).thenReturn(expectedDto);

        RetailerResponseDTO result = retailerService.findById(1L);

        assertThat(result).isEqualTo(expectedDto);
        verify(retailerRepository).findById(1L);
    }

    @Test
    void findById_deveLancarExcecaoQuandoNaoExiste() {
        when(retailerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> retailerService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void create_deveSalvarRetailerCorretamente() {
        RetailerRequestDTO requestDto = new RetailerRequestDTO(
                "Mercado Central", "98765432000188", "contato@mercadocentral.com", "21988887777"
        );

        Retailer retailerToSave = new Retailer();
        Retailer savedRetailer = new Retailer();
        savedRetailer.setId(2L);

        RetailerResponseDTO expectedResponse = new RetailerResponseDTO(
                2L, "Mercado Central", "98765432000188", "contato@mercadocentral.com", "21988887777"
        );

        when(retailerMapper.toEntity(requestDto)).thenReturn(retailerToSave);
        when(retailerRepository.save(retailerToSave)).thenReturn(savedRetailer);
        when(retailerMapper.toResponseDTO(savedRetailer)).thenReturn(expectedResponse);

        RetailerResponseDTO result = retailerService.create(requestDto);

        assertThat(result).isEqualTo(expectedResponse);
        verify(retailerRepository).save(retailerToSave);
    }

    @Test
    void delete_deveLancarExcecaoQuandoRetailerNaoExiste() {
        when(retailerRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> retailerService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(retailerRepository, never()).deleteById(anyLong());
    }
}