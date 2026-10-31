package com.thejavaloom.prodbugs.bug4lazy;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class OrderLine {

    @Id
    @GeneratedValue
    private Long id;
    private String product;
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    private PurchaseOrder order;

    protected OrderLine() {
    }

    OrderLine(PurchaseOrder order, String product, int quantity) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
    }

    public String getProduct() { return product; }
    public int getQuantity() { return quantity; }
}
