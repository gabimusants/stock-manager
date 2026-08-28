package com.gabrielle.stockmanager.repository;

import com.gabrielle.stockmanager.model.Order;
import com.gabrielle.stockmanager.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByRetailerId(Long retailerId);

    List<Order> findByStatus(OrderStatus status);
}
