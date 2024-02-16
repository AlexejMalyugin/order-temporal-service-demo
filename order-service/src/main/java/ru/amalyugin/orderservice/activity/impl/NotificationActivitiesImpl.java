package ru.amalyugin.orderservice.activity.impl;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import io.temporal.spring.boot.ActivityImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ru.amalyugin.orderservice.activity.NotificationActivities;

import static ru.amalyugin.orderservice.constants.TemporalConst.CREATE_ORDER_QUEUE_NAME;

@Slf4j
@Component
@ActivityImpl(taskQueues = CREATE_ORDER_QUEUE_NAME)
public class NotificationActivitiesImpl implements NotificationActivities {

    @Override
    public void sendNotification(NotificationRequestDto input) {
        log.info("Notification for user {} started", input.username());

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
            log.info("Notification for user {} successfully sent", input.username());
        }
    }

}
