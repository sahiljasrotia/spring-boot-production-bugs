package com.thejavaloom.prodbugs.bug5concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.thejavaloom.prodbugs.Concurrently;

/**
 * FAILS ON PURPOSE - the lost updates on screen. The exact number left over changes every run.
 * Run: mvn test -Dtest=LostUpdateBugDemo
 */
@SpringBootTest
class LostUpdateBugDemo {

    @Autowired StockService stockService;

    @BeforeEach
    void reset() {
        stockService.reset(100);
    }

    @Test
    void hundredConcurrentPurchases_shouldLeaveZero() throws InterruptedException {
        Concurrently.run(100, stockService::buyBroken);

        assertThat(stockService.current().get("stock")).as("stock left after 100 purchases").isZero();
    }
}
