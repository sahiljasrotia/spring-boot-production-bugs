package com.thejavaloom.prodbugs.bug5concurrency;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class StockService {

    public static final long ITEM_ID = 1L;
    private static final int MAX_ATTEMPTS = 200;

    private final StockItemRepository stock;
    private final VersionedStockItemRepository versionedStock;
    private final TransactionTemplate tx;

    StockService(StockItemRepository stock, VersionedStockItemRepository versionedStock, TransactionTemplate tx) {
        this.stock = stock;
        this.versionedStock = versionedStock;
        this.tx = tx;
    }

    /**
     * BROKEN under concurrency. Read, subtract in Java, write back.
     * Two requests read 100 at the same time, both write 99 - one sale disappears.
     */
    @Transactional
    public void buyBroken() {
        StockItem item = stock.findById(ITEM_ID).orElseThrow();
        if (item.getStock() > 0) {
            item.setStock(item.getStock() - 1);
        }
    }

    /** FIX A - atomic UPDATE in the database. */
    @Transactional
    public boolean buyAtomic() {
        return stock.decrementIfAvailable(ITEM_ID) == 1;
    }

    /**
     * FIX B - optimistic locking. A stale write is rejected instead of silently winning,
     * so the loser re-reads the fresh value and tries again.
     */
    public void buyOptimistic() {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                tx.executeWithoutResult(status -> {
                    VersionedStockItem item = versionedStock.findById(ITEM_ID).orElseThrow();
                    if (item.getStock() > 0) {
                        item.setStock(item.getStock() - 1);
                    }
                });
                return;
            } catch (ObjectOptimisticLockingFailureException someoneElseWon) {
                sleepBriefly();
            }
        }
        throw new IllegalStateException("gave up after " + MAX_ATTEMPTS + " attempts");
    }

    @Transactional
    public void reset(int quantity) {
        stock.save(new StockItem(ITEM_ID, quantity));
        versionedStock.findById(ITEM_ID).ifPresentOrElse(
                item -> item.setStock(quantity),
                () -> versionedStock.save(new VersionedStockItem(ITEM_ID, quantity)));
    }

    @Transactional(readOnly = true)
    public Map<String, Integer> current() {
        return Map.of(
                "stock", stock.findById(ITEM_ID).map(StockItem::getStock).orElse(-1),
                "versionedStock", versionedStock.findById(ITEM_ID).map(VersionedStockItem::getStock).orElse(-1));
    }

    private static void sleepBriefly() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(1, 6));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
