package ru.amalyugin.orderservice.activity.impl;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import io.temporal.spring.boot.ActivityImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ru.amalyugin.orderservice.activity.ExternalSystemActivities;

import static ru.amalyugin.orderservice.constants.TemporalConst.CREATE_ORDER_QUEUE_NAME;

@Slf4j
@Component
@ActivityImpl(taskQueues = CREATE_ORDER_QUEUE_NAME)
public class ExternalSystemActivitiesImpl implements ExternalSystemActivities {

    @Override
    public ExternalInfoOutputDto getExternalInfo(ExternalInfoDto input) {
        log.info("External call started: {}", input.source().name());
        try {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int sec = random.nextInt(2, 8);
            Thread.sleep(Duration.ofSeconds(sec));
            log.info("External service {} response time: {}", input.source().name(), sec);
        } catch (Exception exception) {
            log.error("Exception when wait external service {}: ", input.source().name(), exception);
        }
        return new ExternalInfoOutputDto(UUID.randomUUID().toString());
    }


}
