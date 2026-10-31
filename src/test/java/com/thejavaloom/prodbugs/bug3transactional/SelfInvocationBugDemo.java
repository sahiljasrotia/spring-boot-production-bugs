package com.thejavaloom.prodbugs.bug3transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.thejavaloom.prodbugs.bug3transactional.PaymentGateway.PaymentFailedException;

/**
 * FAILS ON PURPOSE - this is the bug on screen.
 * Run: mvn test -Dtest=SelfInvocationBugDemo
 */
@SpringBootTest
class SelfInvocationBugDemo {

    @Autowired OrderService orderService;
    @Autowired CustomerOrderRepository orders;
    @Autowired PaymentRecordRepository payments;

    @BeforeEach
    void clean() {
        payments.deleteAll();
        orders.deleteAll();
    }

    /** Same method, called the way production calls it - from inside the class. The order survives. */
    @Test
    void paymentFails_orderShouldBeRolledBack() {
        assertThatThrownBy(() -> orderService.placeOrderBroken("keyboard", true))
                .isInstanceOf(PaymentFailedException.class);

        assertThat(orders.count()).as("orders left in the database after a failed payment").isZero();
    }
}
