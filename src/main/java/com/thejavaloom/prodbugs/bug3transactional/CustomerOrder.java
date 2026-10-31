package com.thejavaloom.prodbugs.bug3transactional;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class CustomerOrder {

    @Id
    @GeneratedValue
    private Long id;
    private String item;

    protected CustomerOrder() {
    }

    public CustomerOrder(String item) {
        this.item = item;
    }

    public Long getId() { return id; }
    public String getItem() { return item; }
}
