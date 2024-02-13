package ru.amalyugin.orderservice.activity.impl;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import io.temporal.spring.boot.ActivityImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ru.amalyugin.orderservice.activity.ExternalSystemActivities;

@Slf4j
@Component
@ActivityImpl(taskQueues = "order-create-queue")
public class ExternalSystemActivitiesImpl implements ExternalSystemActivities {

    @Override
    public String getExternalInfo(ExternalSource source, Long id) {
        log.info("External call started: {}", source.name());
        try {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int sec = random.nextInt(2, 8);
            Thread.sleep(Duration.ofSeconds(sec));
            log.info("External service {} response time: {}", source.name(), sec);
        } catch (Exception exception) {
            log.error("Exception when wait external service {}: ", source.name(), exception);
        }
        return UUID.randomUUID().toString();
    }


}
