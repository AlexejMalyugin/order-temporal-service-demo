package ru.amalyugin.orderservice.activity.impl;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import io.temporal.spring.boot.ActivityImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ru.amalyugin.orderservice.activity.NotificationActivities;

@Slf4j
@Component
@ActivityImpl(taskQueues = "OrderCreateQueue")
public class NotificationActivitiesImpl implements NotificationActivities {

    @Override
    public void sendNotification(String username) {
        log.info("Notification for user {} started", username);

        try {
            int sec = ThreadLocalRandom.current().nextInt(15, 25);
            Thread.sleep(Duration.ofSeconds(sec));
        } catch (Exception exception) {
            log.error("Something wrong");
        }

        int random = ThreadLocalRandom.current().nextInt(0, 3);
        if (random == 0) {
            throw new RuntimeException("Notification has not been delivered");
        } else {
            log.info("Notification for user {} successfully sent", username);
        }
    }

}
