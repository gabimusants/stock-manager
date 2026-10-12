package com.gabrielle.stockmanager.service;

import com.gabrielle.stockmanager.dto.*;
import com.gabrielle.stockmanager.exception.InvalidOrderStatusTransitionException;
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
        product1.setStockQuantity(5);

        Product product2 = new Product();
        product2.setId(20L);
        product2.setPrice(new BigDecimal("3.50"));
        product2.setStockQuantity(4);

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

    @Test
    void updateStatus_paraCancelledDeveDevolverEstoqueDosItens() {
        Product product = new Product();
        product.setId(10L);
        product.setStockQuantity(5); // estoque já reduzido por uma reserva anterior

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(3);

        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        order.addItem(item);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toResponseDTO(order)).thenReturn(mock(OrderResponseDTO.class));

        orderService.updateStatus(1L, OrderStatus.CANCELLED);

        assertThat(product.getStockQuantity()).isEqualTo(8); // 5 + 3 devolvidos
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void updateStatus_deveLancarExcecaoParaTransicaoInvalida() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.SHIPPED); // status final

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateStatus(1L, OrderStatus.PENDING))
                .isInstanceOf(InvalidOrderStatusTransitionException.class);
    }

    @Test
    void create_deveConsolidarQuantidadesQuandoProdutoApareceDuasVezes() {
        Retailer retailer = new Retailer();
        retailer.setId(1L);

        Product product = new Product();
        product.setId(10L);
        product.setPrice(new BigDecimal("5.00"));
        product.setStockQuantity(20);

        OrderRequestDTO requestDto = new OrderRequestDTO(
                1L,
                List.of(
                        new OrderItemRequestDTO(10L, 2),
                        new OrderItemRequestDTO(10L, 3)  // mesmo produto de novo
                )
        );

        when(retailerRepository.findById(1L)).thenReturn(Optional.of(retailer));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderMapper.toResponseDTO(any(Order.class))).thenReturn(mock(OrderResponseDTO.class));

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        orderService.create(requestDto);

        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();

        // Deve ter APENAS 1 item (consolidado), não 2
        assertThat(savedOrder.getItems()).hasSize(1);
        assertThat(savedOrder.getItems().get(0).getQuantity()).isEqualTo(5); // 2 + 3
        assertThat(savedOrder.getTotalAmount()).isEqualByComparingTo("25.00"); // 5 x 5.00
        assertThat(product.getStockQuantity()).isEqualTo(15); // 20 - 5
    }
}