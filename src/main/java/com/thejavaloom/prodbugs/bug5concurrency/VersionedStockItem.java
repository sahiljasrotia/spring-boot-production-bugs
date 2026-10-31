package com.thejavaloom.prodbugs.bug5concurrency;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

/** Same as StockItem plus @Version: Hibernate adds "WHERE version = ?" to every UPDATE. */
@Entity
public class VersionedStockItem {

    @Id
    private Long id;
    private int stock;

    @Version
    private long version;

    protected VersionedStockItem() {
    }

    public VersionedStockItem(Long id, int stock) {
        this.id = id;
        this.stock = stock;
    }

    public Long getId() { return id; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
