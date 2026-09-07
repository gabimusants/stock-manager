package com.gabrielle.stockmanager.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabrielle.stockmanager.dto.OrderItemRequestDTO;
import com.gabrielle.stockmanager.dto.OrderRequestDTO;
import com.gabrielle.stockmanager.dto.ProductRequestDTO;
import com.gabrielle.stockmanager.dto.RetailerRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class OrderIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deveCriarPedidoCompletoDeVerdadeNoBanco() throws Exception {
        // Arrange: cria um Retailer real via HTTP
        RetailerRequestDTO retailerDto = new RetailerRequestDTO(
                "Bar do Zé", "12345678000199", "contato@bardoze.com", "21999999999"
        );

        String retailerResponse = mockMvc.perform(post("/api/retailers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(retailerDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long retailerId = objectMapper.readTree(retailerResponse).get("id").asLong();

        // Arrange: cria um Product real via HTTP
        ProductRequestDTO productDto = new ProductRequestDTO(
                "SKU-TEST-01", "Cerveja Pilsen", "350ml", new BigDecimal("5.00"), 100
        );

        String productResponse = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long productId = objectMapper.readTree(productResponse).get("id").asLong();

        // Act: cria o Order referenciando os ids reais
        OrderRequestDTO orderDto = new OrderRequestDTO(
                retailerId,
                List.of(new OrderItemRequestDTO(productId, 4))
        );

        // Assert: valida a resposta HTTP completa
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.retailerId").value(retailerId))
                .andExpect(jsonPath("$.totalAmount").value(20.00))
                .andExpect(jsonPath("$.items[0].quantity").value(4))
                .andExpect(jsonPath("$.items[0].productName").value("Cerveja Pilsen"));
    }

    @Test
    void deveRetornar404QuandoRetailerNaoExiste() throws Exception {
        OrderRequestDTO orderDto = new OrderRequestDTO(99999L, List.of(new OrderItemRequestDTO(1L, 1)));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}