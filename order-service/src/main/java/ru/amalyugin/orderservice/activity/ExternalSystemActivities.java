package ru.amalyugin.orderservice.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface ExternalSystemActivities {

    @ActivityMethod
    ExternalInfoOutputDto getExternalInfo(ExternalInfoDto input);

    record ExternalInfoDto(
            ExternalSource source,
            Long id
    ) {
    }

    record ExternalInfoOutputDto(String input) {}

    enum ExternalSource {
        VENDOR,
        ORG
    }
}
