package com.thejavaloom.prodbugs;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** Starts `times` calls at the same instant, the way real traffic arrives. */
public final class Concurrently {

    private Concurrently() {
    }

    public static void run(int times, Runnable action) throws InterruptedException {
        CountDownLatch startLine = new CountDownLatch(1);
        try (ExecutorService pool = Executors.newFixedThreadPool(32)) {
            for (int i = 0; i < times; i++) {
                pool.submit(() -> {
                    startLine.await();
                    action.run();
                    return null;
                });
            }
            startLine.countDown();
            pool.shutdown();
            pool.awaitTermination(1, TimeUnit.MINUTES);
        }
    }
}
