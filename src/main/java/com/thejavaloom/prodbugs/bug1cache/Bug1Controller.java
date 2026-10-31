package com.thejavaloom.prodbugs.bug1cache;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bug1")
public class Bug1Controller {

    private final CatalogItemRepository items;
    private final LocalCachePriceService local;
    private final SharedCachePriceService shared;
    private final String instance;

    Bug1Controller(CatalogItemRepository items, LocalCachePriceService local, SharedCachePriceService shared,
                   @Value("${demo.instance}") String instance) {
        this.items = items;
        this.local = local;
        this.shared = shared;
        this.instance = instance;
    }

    /** Sets item 1 back to 999. Restart the instances too, so their local caches start empty. */
    @PostMapping("/reset")
    Map<String, Object> reset() {
        items.save(new CatalogItem(1L, "Mechanical keyboard", new BigDecimal("999")));
        return Map.of("instance", instance, "price", 999);
    }

    @GetMapping("/{mode}/price/{id}")
    Map<String, Object> price(@PathVariable String mode, @PathVariable Long id) {
        BigDecimal price = "fixed".equals(mode) ? shared.getPrice(id) : local.getPrice(id);
        return Map.of("instance", instance, "mode", mode, "price", price);
    }

    @PutMapping("/{mode}/price/{id}")
    Map<String, Object> update(@PathVariable String mode, @PathVariable Long id, @RequestParam BigDecimal value) {
        if ("fixed".equals(mode)) shared.updatePrice(id, value);
        else local.updatePrice(id, value);
        return Map.of("instance", instance, "mode", mode, "updatedTo", value);
    }
}
