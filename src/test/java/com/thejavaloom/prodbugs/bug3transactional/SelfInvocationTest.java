package com.thejavaloom.prodbugs.bug3transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.thejavaloom.prodbugs.bug3transactional.PaymentGateway.PaymentFailedException;

@SpringBootTest
class SelfInvocationTest {

    @Autowired OrderService orderService;
    @Autowired CustomerOrderRepository orders;
    @Autowired PaymentRecordRepository payments;

    @BeforeEach
    void clean() {
        payments.deleteAll();
        orders.deleteAll();
    }

    /** The test most teams write. It calls the @Transactional method directly - through the proxy - and passes. */
    @Test
    void directCallRollsBack_soTheBugLooksImpossible() {
        assertThatThrownBy(() -> orderService.saveOrderAndPayment("keyboard", true))
                .isInstanceOf(PaymentFailedException.class);

        assertThat(orders.count()).isZero();
    }

    @Test
    void fixedEntryPointRollsBack() {
        assertThatThrownBy(() -> orderService.placeOrderFixed("keyboard", true))
                .isInstanceOf(PaymentFailedException.class);

        assertThat(orders.count()).isZero();
    }
}
