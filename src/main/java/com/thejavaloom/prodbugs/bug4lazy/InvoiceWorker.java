package com.thejavaloom.prodbugs.bug4lazy;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Runs off the request thread - like a Kafka listener, a @Scheduled job or an @Async task.
 * There is no open-in-view here: the session closes as soon as findById returns.
 */
@Service
public class InvoiceWorker {

    private final PurchaseOrderRepository orders;

    InvoiceWorker(PurchaseOrderRepository orders) {
        this.orders = orders;
    }

    /** BROKEN. Touching getLines() after the session closed throws LazyInitializationException. */
    @Async
    public CompletableFuture<String> buildInvoiceBroken(Long orderId) {
        PurchaseOrder order = orders.findById(orderId).orElseThrow();
        return CompletableFuture.completedFuture(invoice(order));
    }

    /** FIXED. The lines are fetched in the same query, so nothing is lazy any more. */
    @Async
    public CompletableFuture<String> buildInvoiceFixed(Long orderId) {
        PurchaseOrder order = orders.findWithLinesById(orderId).orElseThrow();
        return CompletableFuture.completedFuture(invoice(order));
    }

    static String invoice(PurchaseOrder order) {
        return order.getCustomer() + ": " + order.getLines().size() + " lines";
    }
}
