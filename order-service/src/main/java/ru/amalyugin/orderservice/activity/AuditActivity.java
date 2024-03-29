package ru.amalyugin.orderservice.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import ru.amalyugin.orderservice.dto.AuditRecordDto;

@ActivityInterface
public interface AuditActivity {

    @ActivityMethod
    void saveAuditRecord(AuditRecordDto dto);

}
