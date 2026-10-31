package com.thejavaloom.prodbugs.bug1cache;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class CatalogItem {

    @Id
    private Long id;
    private String name;
    private BigDecimal price;

    protected CatalogItem() {
    }

    public CatalogItem(Long id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
