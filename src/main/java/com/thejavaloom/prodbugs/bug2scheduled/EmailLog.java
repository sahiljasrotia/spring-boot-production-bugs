package com.thejavaloom.prodbugs.bug2scheduled;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

/** Stands in for a real email: one row per email "sent". Duplicates are easy to count. */
@Entity
public class EmailLog {

    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    private String sentByInstance;
    private Instant sentAt;

    protected EmailLog() {
    }

    public EmailLog(Long userId, String sentByInstance) {
        this.userId = userId;
        this.sentByInstance = sentByInstance;
        this.sentAt = Instant.now();
    }

    public Long getUserId() { return userId; }
    public String getSentByInstance() { return sentByInstance; }
    public Instant getSentAt() { return sentAt; }
}
