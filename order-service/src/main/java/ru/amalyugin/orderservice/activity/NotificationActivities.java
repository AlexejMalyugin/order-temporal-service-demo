package ru.amalyugin.orderservice.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface NotificationActivities {

    @ActivityMethod
    void sendNotification(NotificationRequestDto input);

    record NotificationRequestDto(String username) {
    }

}
