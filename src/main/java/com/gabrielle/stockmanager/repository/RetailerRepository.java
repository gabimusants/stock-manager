package com.gabrielle.stockmanager.repository;

import com.gabrielle.stockmanager.model.Retailer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RetailerRepository extends JpaRepository<Retailer, Long> {

    Optional<Retailer> findByCnpj(String cnpj);

    boolean existsByCnpj(String cnpj);
}
