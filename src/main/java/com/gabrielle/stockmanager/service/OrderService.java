package com.gabrielle.stockmanager.service;

import com.gabrielle.stockmanager.dto.OrderItemRequestDTO;
import com.gabrielle.stockmanager.dto.OrderRequestDTO;
import com.gabrielle.stockmanager.dto.OrderResponseDTO;
import com.gabrielle.stockmanager.exception.ResourceNotFoundException;
import com.gabrielle.stockmanager.mapper.OrderMapper;
import com.gabrielle.stockmanager.model.*;
import com.gabrielle.stockmanager.repository.OrderRepository;
import com.gabrielle.stockmanager.repository.ProductRepository;
import com.gabrielle.stockmanager.repository.RetailerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final RetailerRepository retailerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository,
                        RetailerRepository retailerRepository,
                        ProductRepository productRepository,
                        OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.retailerRepository = retailerRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
    }

    public OrderResponseDTO create(OrderRequestDTO dto) {
        Retailer retailer = retailerRepository.findById(dto.retailerId())
                .orElseThrow(() -> new ResourceNotFoundException("Retailer não encontrado: " + dto.retailerId()));

        Order order = new Order();
        order.setRetailer(retailer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        Map<Long, Integer> quantidadesPorProduto = new LinkedHashMap<>();
        for (OrderItemRequestDTO itemDto : dto.items()) {
            quantidadesPorProduto.merge(itemDto.productId(), itemDto.quantity(), Integer::sum);
        }

        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : quantidadesPorProduto.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + productId));

            product.reserveStock(quantity);

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setUnitPriceAtPurchase(product.getPrice());

            order.addItem(item);

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
            total = total.add(subtotal);
        }

        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        return orderMapper.toResponseDTO(saved);
    }

    public List<OrderResponseDTO> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponseDTO)
                .toList();
    }

    public OrderResponseDTO findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order não encontrado: " + id));
        return orderMapper.toResponseDTO(order);
    }

    @Transactional
    public OrderResponseDTO updateStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order não encontrado: " + id));

        if (newStatus == OrderStatus.CANCELLED) {
            // Regra de negócio: cancelamento devolve o estoque reservado
            for (OrderItem item : order.getItems()) {
                item.getProduct().releaseStock(item.getQuantity());
            }
        }

        order.transitionTo(newStatus);

        return orderMapper.toResponseDTO(order);
    }
}