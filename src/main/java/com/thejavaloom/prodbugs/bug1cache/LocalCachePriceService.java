package com.thejavaloom.prodbugs.bug1cache;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * BROKEN in production. The eviction is correct - on one instance this works perfectly.
 * But the cache is in local memory, so @CacheEvict only clears THIS instance's copy.
 * Every other instance keeps serving the old price.
 */
@Service
public class LocalCachePriceService {

    private static final Logger log = LoggerFactory.getLogger(LocalCachePriceService.class);
    private final CatalogItemRepository items;

    LocalCachePriceService(CatalogItemRepository items) {
        this.items = items;
    }

    @Cacheable(cacheNames = "price-local", key = "#id")
    public BigDecimal getPrice(Long id) {
        log.info("cache MISS - reading price of item {} from the database", id);
        return items.findById(id).orElseThrow().getPrice();
    }

    @CacheEvict(cacheNames = "price-local", key = "#id")
    @Transactional
    public void updatePrice(Long id, BigDecimal price) {
        items.findById(id).orElseThrow().setPrice(price);
    }
}
