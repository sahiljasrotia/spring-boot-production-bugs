package com.thejavaloom.prodbugs.bug2scheduled;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bug2")
public class Bug2Controller {

    private final EmailLogRepository emails;

    Bug2Controller(EmailLogRepository emails) {
        this.emails = emails;
    }

    /** Emails per user. Anything above 1 per run is a duplicate. */
    @GetMapping("/emails")
    Map<String, Object> emails() {
        var all = emails.findAll();
        Map<Long, Long> perUser = all.stream()
                .collect(Collectors.groupingBy(EmailLog::getUserId, TreeMap::new, Collectors.counting()));
        Map<String, Long> perInstance = all.stream()
                .collect(Collectors.groupingBy(EmailLog::getSentByInstance, TreeMap::new, Collectors.counting()));
        return Map.of("total", all.size(), "emailsPerUser", perUser, "sentByInstance", perInstance);
    }

    @DeleteMapping("/emails")
    Map<String, Object> clear() {
        emails.deleteAll();
        return Map.of("cleared", true);
    }
}
