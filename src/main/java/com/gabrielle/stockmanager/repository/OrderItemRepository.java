package com.gabrielle.stockmanager.repository;

import com.gabrielle.stockmanager.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
