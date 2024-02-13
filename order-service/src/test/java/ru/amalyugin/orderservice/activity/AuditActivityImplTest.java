package ru.amalyugin.orderservice.activity;

import io.temporal.testing.TestActivityExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import ru.amalyugin.orderservice.activity.impl.AuditActivityImpl;
import ru.amalyugin.orderservice.dto.AuditRecordDto;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuditActivityImplTest {

    @RegisterExtension
    public static final TestActivityExtension activityExtension = TestActivityExtension.newBuilder()
            .setActivityImplementations(new AuditActivityImpl())
            .build();

    @Test
    void saveAuditRecordTest(AuditActivity activity) {
        AuditRecordDto recordDto = new AuditRecordDto(
                UUID.randomUUID().toString(),
                System.currentTimeMillis(),
                AuditRecordDto.AuditStatus.SUCCESS
        );

        assertDoesNotThrow(() -> activity.saveAuditRecord(recordDto));
    }
}