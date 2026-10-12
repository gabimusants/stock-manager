package com.gabrielle.stockmanager.client;

import com.gabrielle.stockmanager.dto.CatalogItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;
import java.util.List;

@Component
public class CatalogServiceClient {
    private static final Logger log = LoggerFactory.getLogger(CatalogServiceClient.class);
    private final RestClient restClient;

    public CatalogServiceClient(@Value("${catalog-service.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public Optional<CatalogItemDTO> findBySku(String sku) {
        try {
            CatalogItemDTO item = restClient.get().uri("/api/catalog-items/sku/{sku}", sku).retrieve().body(CatalogItemDTO.class);
            return Optional.ofNullable(item);
        } catch (HttpClientErrorException.NotFound e) {
            log.info("Nenhum item de catálogo encontrado para SKU {}", sku, e);
            return Optional.empty();
        } catch (RestClientException e) {
            log.warn("Falha ao se comunicar com catalog-service para SKU {}", sku, e);
            return Optional.empty();
        }
    }
}
