package com.thejavaloom.prodbugs.bug3transactional;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class PaymentRecord {

    @Id
    @GeneratedValue
    private Long id;
    private Long orderId;

    protected PaymentRecord() {
    }

    public PaymentRecord(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderId() { return orderId; }
}
