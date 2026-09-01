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
import java.util.List;

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

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequestDTO itemDto : dto.items()) {
            Product product = productRepository.findById(itemDto.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + itemDto.productId()));

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemDto.quantity());
            item.setUnitPriceAtPurchase(product.getPrice());

            order.addItem(item);

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemDto.quantity()));
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
}