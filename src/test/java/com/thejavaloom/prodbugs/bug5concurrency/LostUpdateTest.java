package com.thejavaloom.prodbugs.bug5concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.thejavaloom.prodbugs.Concurrently;

@SpringBootTest
class LostUpdateTest {

    @Autowired StockService stockService;

    @BeforeEach
    void reset() {
        stockService.reset(100);
    }

    /** The test that ships with the broken code - one request at a time. It passes. */
    @Test
    void sequentialPurchasesLookFine() {
        for (int i = 0; i < 100; i++) stockService.buyBroken();

        assertThat(stockService.current().get("stock")).isZero();
    }

    @Test
    void atomicUpdateSurvivesConcurrentPurchases() throws InterruptedException {
        Concurrently.run(100, stockService::buyAtomic);

        assertThat(stockService.current().get("stock")).isZero();
    }

    @Test
    void optimisticLockingSurvivesConcurrentPurchases() throws InterruptedException {
        Concurrently.run(100, stockService::buyOptimistic);

        assertThat(stockService.current().get("versionedStock")).isZero();
    }
}
