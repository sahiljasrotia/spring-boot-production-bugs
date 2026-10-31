package com.thejavaloom.prodbugs.bug4lazy;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class PurchaseOrder {

    @Id
    @GeneratedValue
    private Long id;
    private String customer;

    // @OneToMany is LAZY by default: the lines are loaded only when first touched,
    // and only if the Hibernate session is still open at that moment.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderLine> lines = new ArrayList<>();

    protected PurchaseOrder() {
    }

    public PurchaseOrder(String customer) {
        this.customer = customer;
    }

    public void addLine(String product, int quantity) {
        lines.add(new OrderLine(this, product, quantity));
    }

    public Long getId() { return id; }
    public String getCustomer() { return customer; }
    public List<OrderLine> getLines() { return lines; }
}
