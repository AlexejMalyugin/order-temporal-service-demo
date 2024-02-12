package ru.amalyugin.orderservice.activity.impl;

import io.temporal.spring.boot.ActivityImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ru.amalyugin.orderservice.activity.AuditActivity;
import ru.amalyugin.orderservice.dto.AuditRecordDto;

@Slf4j
@Component
@ActivityImpl(taskQueues = "OrderCreateQueue")
public class AuditActivityImpl implements AuditActivity {

    @Override
    public void saveAuditRecord(AuditRecordDto dto) {
        log.info("Audit saved: {}", dto);
    }

}
