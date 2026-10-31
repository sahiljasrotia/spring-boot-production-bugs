package com.thejavaloom.prodbugs.bug2scheduled;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * FIXED. Every instance still fires, but only the one that grabs the row in the shedlock table runs the job.
 * lockAtLeastFor matters: the job finishes in milliseconds, and without it a slightly late instance
 * would find the lock already released and send the emails again.
 */
@Component
@ConditionalOnProperty(name = "demo.bug2.mode", havingValue = "fixed")
public class DailyReportJobFixed {

    private final DailyReportSender sender;

    DailyReportJobFixed(DailyReportSender sender) {
        this.sender = sender;
    }

    @Scheduled(cron = "${demo.bug2.cron}")
    @SchedulerLock(name = "dailyReport", lockAtLeastFor = "PT20S", lockAtMostFor = "PT5M")
    public void sendDailyReport() {
        sender.sendToAllUsers();
    }
}
