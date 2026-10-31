package com.thejavaloom.prodbugs.bug4lazy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LazyLoadingTest {

    @Autowired InvoiceWorker worker;
    @Autowired PurchaseOrderRepository orders;
    Long orderId;

    @BeforeEach
    void seed() {
        PurchaseOrder order = new PurchaseOrder("Asha");
        order.addLine("keyboard", 1);
        order.addLine("cable", 2);
        orderId = orders.save(order).getId();
    }

    @Test
    void entityGraphLoadsTheLinesOffTheRequestThread() {
        assertThat(worker.buildInvoiceFixed(orderId).join()).isEqualTo("Asha: 2 lines");
    }
}
