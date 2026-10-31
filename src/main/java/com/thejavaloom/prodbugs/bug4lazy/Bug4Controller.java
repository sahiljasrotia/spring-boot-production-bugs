package com.thejavaloom.prodbugs.bug4lazy;

import java.util.Map;
import java.util.concurrent.CompletionException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bug4")
public class Bug4Controller {

    private final PurchaseOrderRepository orders;
    private final InvoiceWorker worker;

    Bug4Controller(PurchaseOrderRepository orders, InvoiceWorker worker) {
        this.orders = orders;
        this.worker = worker;
    }

    @PostMapping("/seed")
    Map<String, Object> seed() {
        PurchaseOrder order = new PurchaseOrder("Asha");
        order.addLine("Mechanical keyboard", 1);
        order.addLine("USB-C cable", 2);
        order.addLine("Desk mat", 1);
        return Map.of("orderId", orders.save(order).getId());
    }

    /** Works - but only because open-in-view keeps the session open for the whole HTTP request. */
    @GetMapping("/controller/{id}")
    Map<String, Object> fromController(@PathVariable Long id) {
        return Map.of("path", "controller", "invoice", InvoiceWorker.invoice(orders.findById(id).orElseThrow()));
    }

    @GetMapping("/async/{mode}/{id}")
    ResponseEntity<Map<String, Object>> fromAsync(@PathVariable String mode, @PathVariable Long id) {
        try {
            String invoice = ("fixed".equals(mode) ? worker.buildInvoiceFixed(id) : worker.buildInvoiceBroken(id)).join();
            return ResponseEntity.ok(Map.of("path", "async", "mode", mode, "invoice", invoice));
        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            return ResponseEntity.internalServerError().body(Map.of("path", "async", "mode", mode,
                    "error", cause.getClass().getSimpleName() + ": " + cause.getMessage()));
        }
    }
}
