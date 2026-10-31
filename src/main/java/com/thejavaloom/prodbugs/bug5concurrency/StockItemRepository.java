package com.thejavaloom.prodbugs.bug5concurrency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockItemRepository extends JpaRepository<StockItem, Long> {

    /** Fix A: the database does the read and the write in one statement. Nothing to lose. */
    @Modifying
    @Query("update StockItem s set s.stock = s.stock - 1 where s.id = :id and s.stock > 0")
    int decrementIfAvailable(@Param("id") Long id);
}
