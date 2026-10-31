package com.thejavaloom.prodbugs.bug3transactional;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thejavaloom.prodbugs.bug3transactional.PaymentGateway.PaymentFailedException;

@RestController
@RequestMapping("/bug3")
public class Bug3Controller {

    private final OrderService orderService;
    private final CustomerOrderRepository orders;
    private final PaymentRecordRepository payments;

    Bug3Controller(OrderService orderService, CustomerOrderRepository orders, PaymentRecordRepository payments) {
        this.orderService = orderService;
        this.orders = orders;
        this.payments = payments;
    }

    @PostMapping("/{mode}/orders")
    ResponseEntity<Map<String, Object>> place(@PathVariable String mode,
                                              @RequestParam(defaultValue = "true") boolean failPayment) {
        try {
            if ("fixed".equals(mode)) orderService.placeOrderFixed("Mechanical keyboard", failPayment);
            else orderService.placeOrderBroken("Mechanical keyboard", failPayment);
            return ResponseEntity.ok(state(mode, "payment ok"));
        } catch (PaymentFailedException e) {
            return ResponseEntity.status(402).body(state(mode, e.getMessage()));
        }
    }

    @GetMapping("/orders")
    Map<String, Object> orders() {
        return state("-", "-");
    }

    @DeleteMapping("/orders")
    Map<String, Object> clear() {
        payments.deleteAll();
        orders.deleteAll();
        return state("-", "cleared");
    }

    private Map<String, Object> state(String mode, String result) {
        return Map.of("mode", mode, "result", result,
                "ordersInDb", orders.count(), "paymentsInDb", payments.count());
    }
}
