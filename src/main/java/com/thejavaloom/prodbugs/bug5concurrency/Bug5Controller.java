package com.thejavaloom.prodbugs.bug5concurrency;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bug5")
public class Bug5Controller {

    private final StockService stockService;

    Bug5Controller(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/reset")
    Map<String, Integer> reset(@RequestParam(defaultValue = "100") int stock) {
        stockService.reset(stock);
        return stockService.current();
    }

    @GetMapping("/stock")
    Map<String, Integer> stock() {
        return stockService.current();
    }

    /** Fires `requests` purchases at the same time and reports what is left. Expected: 0. */
    @PostMapping("/{mode}/load")
    Map<String, Object> load(@PathVariable String mode, @RequestParam(defaultValue = "100") int requests)
            throws InterruptedException {
        Runnable buy = switch (mode) {
            case "atomic" -> stockService::buyAtomic;
            case "optimistic" -> stockService::buyOptimistic;
            default -> stockService::buyBroken;
        };
        try (ExecutorService pool = Executors.newFixedThreadPool(20)) {
            for (int i = 0; i < requests; i++) pool.submit(buy);
            pool.shutdown();
            pool.awaitTermination(1, TimeUnit.MINUTES);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("mode", mode);
        result.put("purchases", requests);
        result.putAll(stockService.current());
        return result;
    }
}
