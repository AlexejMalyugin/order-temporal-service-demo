package ru.amalyugin.orderservice.activity.impl;

import java.util.UUID;

import io.temporal.spring.boot.ActivityImpl;
import org.springframework.stereotype.Component;

import ru.amalyugin.orderservice.activity.UtilActivity;

import static ru.amalyugin.orderservice.constants.TemporalConst.CREATE_ORDER_QUEUE_NAME;

@Component
@ActivityImpl(taskQueues = CREATE_ORDER_QUEUE_NAME)
public class UtilActivityImpl implements UtilActivity {

    @Override
    public String generateUUID() {
        return UUID.randomUUID().toString();
    }

    @Override
    public long generateLong() {
        return UUID.randomUUID().timestamp();
    }

}
