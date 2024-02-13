package ru.amalyugin.orderservice.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface ExternalSystemActivities {

    @ActivityMethod
    String getExternalInfo(ExternalSource source, Long id);

    enum ExternalSource {
        VENDOR,
        ORG
    }
}
