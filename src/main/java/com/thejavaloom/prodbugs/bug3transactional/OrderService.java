package com.thejavaloom.prodbugs.bug3transactional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final CustomerOrderRepository orders;
    private final PaymentGateway paymentGateway;
    private final OrderWriter orderWriter;

    OrderService(CustomerOrderRepository orders, PaymentGateway paymentGateway, OrderWriter orderWriter) {
        this.orders = orders;
        this.paymentGateway = paymentGateway;
        this.orderWriter = orderWriter;
    }

    /**
     * BROKEN. This is how production calls it. `this.saveOrderAndPayment(...)` is a plain Java call
     * on the real object, not on Spring's proxy - so @Transactional below is never applied.
     * The order is committed on its own, the payment fails, nothing rolls back.
     */
    public void placeOrderBroken(String item, boolean failPayment) {
        this.saveOrderAndPayment(item, failPayment);
    }

    /**
     * Looks correct, and a test that calls it DIRECTLY passes - because the test goes through the proxy.
     * That is why this bug survives code review and the test suite.
     */
    @Transactional
    public void saveOrderAndPayment(String item, boolean failPayment) {
        CustomerOrder order = orders.save(new CustomerOrder(item));
        paymentGateway.charge(order, failPayment);
    }

    /** FIXED. The transactional method lives in another bean, so the call goes through its proxy. */
    public void placeOrderFixed(String item, boolean failPayment) {
        orderWriter.saveOrderAndPayment(item, failPayment);
    }
}
