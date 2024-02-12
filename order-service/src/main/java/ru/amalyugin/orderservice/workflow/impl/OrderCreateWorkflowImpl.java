package ru.amalyugin.orderservice.workflow.impl;

import io.temporal.activity.ActivityOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;

import ru.amalyugin.orderservice.activity.AuditActivity;
import ru.amalyugin.orderservice.dto.AuditRecordDto;
import ru.amalyugin.orderservice.dto.OrderToCreateDto;
import ru.amalyugin.orderservice.workflow.OrderCreateWorkflow;

import java.time.Duration;
import java.util.UUID;

@WorkflowImpl(taskQueues = "OrderCreateQueue")
public class OrderCreateWorkflowImpl implements OrderCreateWorkflow {

    private final AuditActivity auditActivity = Workflow.newActivityStub(
            AuditActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(2))
                    .build());

    @Override
    public String createOrder(OrderToCreateDto dto) {
        var auditRecordDto = new AuditRecordDto(
                UUID.randomUUID().toString(),
                dto.requestId(),
                AuditRecordDto.AuditStatus.SUCCESS
        );

        auditActivity.saveAuditRecord(auditRecordDto);

        return dto.description();
    }

}
