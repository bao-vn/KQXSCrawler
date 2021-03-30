package com.example.heroku.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduledCrawlerService {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(cron = "0 0 16,17,18 * * *")
//    @Scheduled(fixedRate = 1000)
    public void scheduledCrawl() {
        log.info("The time is now {}", dateFormat.format(new Date()));
    }
}
