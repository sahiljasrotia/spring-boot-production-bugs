package com.thejavaloom.prodbugs.bug2scheduled;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DailyReportSender {

    private static final Logger log = LoggerFactory.getLogger(DailyReportSender.class);
    private static final List<Long> USERS = List.of(1L, 2L, 3L);

    private final EmailLogRepository emails;
    private final String instance;

    DailyReportSender(EmailLogRepository emails, @Value("${demo.instance}") String instance) {
        this.emails = emails;
        this.instance = instance;
    }

    public void sendToAllUsers() {
        for (Long userId : USERS) {
            log.info("Sending daily report to user {}", userId);
            emails.save(new EmailLog(userId, instance));
        }
    }
}
