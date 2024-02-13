package ru.amalyugin.orderservice.workflow.impl;

import java.time.Duration;
import java.util.UUID;

import io.temporal.activity.ActivityOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;

import ru.amalyugin.orderservice.activity.AuditActivity;
import ru.amalyugin.orderservice.activity.OrderDaoActivity;
import ru.amalyugin.orderservice.dto.AuditRecordDto;
import ru.amalyugin.orderservice.dto.OrderToCreateDto;
import ru.amalyugin.orderservice.workflow.OrderCreateWorkflow;

@WorkflowImpl(taskQueues = "OrderCreateQueue")
public class OrderCreateWorkflowImpl implements OrderCreateWorkflow {

    private final AuditActivity auditActivity = Workflow.newActivityStub(
            AuditActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(2))
                    .build());

    private final OrderDaoActivity orderDaoActivity = Workflow.newActivityStub(
            OrderDaoActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(3))
                    .build()
    );

    @Override
    public String createOrder(OrderToCreateDto dto) {
        String uuid = UUID.randomUUID().toString();

        try {
            orderDaoActivity.saveOrder(dto);
        } catch (Exception exception) {
            auditActivity.saveAuditRecord(new AuditRecordDto(
                    uuid,
                    dto.requestId(),
                    AuditRecordDto.AuditStatus.ERROR
            ));
        }

        auditActivity.saveAuditRecord(new AuditRecordDto(
                uuid,
                dto.requestId(),
                AuditRecordDto.AuditStatus.SUCCESS
        ));

        return dto.description();
    }

}
