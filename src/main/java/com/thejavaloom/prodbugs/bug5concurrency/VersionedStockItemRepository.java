package com.thejavaloom.prodbugs.bug5concurrency;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VersionedStockItemRepository extends JpaRepository<VersionedStockItem, Long> {
}
