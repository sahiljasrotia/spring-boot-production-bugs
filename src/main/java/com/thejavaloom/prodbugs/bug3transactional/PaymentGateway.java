package com.thejavaloom.prodbugs.bug3transactional;

import org.springframework.stereotype.Component;

@Component
public class PaymentGateway {

    private final PaymentRecordRepository payments;

    PaymentGateway(PaymentRecordRepository payments) {
        this.payments = payments;
    }

    public void charge(CustomerOrder order, boolean fail) {
        if (fail) {
            throw new PaymentFailedException("card declined for order " + order.getId());
        }
        payments.save(new PaymentRecord(order.getId()));
    }

    public static class PaymentFailedException extends RuntimeException {
        public PaymentFailedException(String message) {
            super(message);
        }
    }
}
