package ru.amalyugin.orderservice.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface UtilActivity {

    @ActivityMethod
    String generateUUID();

    @ActivityMethod
    long generateLong();

}
