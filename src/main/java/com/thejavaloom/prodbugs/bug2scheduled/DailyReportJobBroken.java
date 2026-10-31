package com.thejavaloom.prodbugs.bug2scheduled;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * BROKEN in production. @Scheduled runs inside each JVM and knows nothing about other instances.
 * One instance: one email per user. Three instances: three emails per user.
 */
@Component
@ConditionalOnProperty(name = "demo.bug2.mode", havingValue = "broken")
public class DailyReportJobBroken {

    private final DailyReportSender sender;

    DailyReportJobBroken(DailyReportSender sender) {
        this.sender = sender;
    }

    @Scheduled(cron = "${demo.bug2.cron}")
    public void sendDailyReport() {
        sender.sendToAllUsers();
    }
}
