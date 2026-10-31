package com.thejavaloom.prodbugs.bug3transactional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderWriter {

    private final CustomerOrderRepository orders;
    private final PaymentGateway paymentGateway;

    OrderWriter(CustomerOrderRepository orders, PaymentGateway paymentGateway) {
        this.orders = orders;
        this.paymentGateway = paymentGateway;
    }

    @Transactional
    public void saveOrderAndPayment(String item, boolean failPayment) {
        CustomerOrder order = orders.save(new CustomerOrder(item));
        paymentGateway.charge(order, failPayment);
    }
}
