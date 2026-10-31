package com.thejavaloom.prodbugs.bug1cache;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** FIXED. Same code, but the cache is shared by every instance - one eviction clears it everywhere. */
@Service
public class SharedCachePriceService {

    private static final Logger log = LoggerFactory.getLogger(SharedCachePriceService.class);
    private final CatalogItemRepository items;

    SharedCachePriceService(CatalogItemRepository items) {
        this.items = items;
    }

    @Cacheable(cacheNames = "price-shared", key = "#id", cacheManager = "sharedCacheManager")
    public BigDecimal getPrice(Long id) {
        log.info("cache MISS - reading price of item {} from the database", id);
        return items.findById(id).orElseThrow().getPrice();
    }

    @CacheEvict(cacheNames = "price-shared", key = "#id", cacheManager = "sharedCacheManager")
    @Transactional
    public void updatePrice(Long id, BigDecimal price) {
        items.findById(id).orElseThrow().setPrice(price);
    }
}
