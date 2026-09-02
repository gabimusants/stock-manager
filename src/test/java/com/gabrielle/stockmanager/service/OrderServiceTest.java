package com.gabrielle.stockmanager.service;

import com.gabrielle.stockmanager.dto.*;
import com.gabrielle.stockmanager.exception.ResourceNotFoundException;
import com.gabrielle.stockmanager.mapper.OrderMapper;
import com.gabrielle.stockmanager.model.*;
import com.gabrielle.stockmanager.repository.OrderRepository;
import com.gabrielle.stockmanager.repository.ProductRepository;
import com.gabrielle.stockmanager.repository.RetailerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RetailerRepository retailerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    @Test
    void create_deveCalcularTotalAmountCorretamenteComMultiplosItens() {
        // Arrange
        Retailer retailer = new Retailer();
        retailer.setId(1L);

        Product product1 = new Product();
        product1.setId(10L);
        product1.setPrice(new BigDecimal("5.00"));

        Product product2 = new Product();
        product2.setId(20L);
        product2.setPrice(new BigDecimal("3.50"));

        OrderRequestDTO requestDto = new OrderRequestDTO(
                1L,
                List.of(
                        new OrderItemRequestDTO(10L, 2),  // 2 x 5.00 = 10.00
                        new OrderItemRequestDTO(20L, 3)   // 3 x 3.50 = 10.50
                )
                // total esperado: 20.50
        );

        when(retailerRepository.findById(1L)).thenReturn(Optional.of(retailer));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(20L)).thenReturn(Optional.of(product2));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderMapper.toResponseDTO(any(Order.class))).thenReturn(mock(OrderResponseDTO.class));

        // Act
        orderService.create(requestDto);

        // Assert - capturamos o Order que foi passado para o save()
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getTotalAmount()).isEqualByComparingTo("20.50");
        assertThat(savedOrder.getItems()).hasSize(2);
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void create_deveLancarExcecaoQuandoRetailerNaoExiste() {
        OrderRequestDTO requestDto = new OrderRequestDTO(999L, List.of());

        when(retailerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.create(requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");

        verify(orderRepository, never()).save(any());
    }

    @Test
    void create_deveLancarExcecaoQuandoProdutoNaoExiste() {
        Retailer retailer = new Retailer();
        retailer.setId(1L);

        OrderRequestDTO requestDto = new OrderRequestDTO(
                1L,
                List.of(new OrderItemRequestDTO(999L, 1))
        );

        when(retailerRepository.findById(1L)).thenReturn(Optional.of(retailer));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.create(requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");

        verify(orderRepository, never()).save(any());
    }
}