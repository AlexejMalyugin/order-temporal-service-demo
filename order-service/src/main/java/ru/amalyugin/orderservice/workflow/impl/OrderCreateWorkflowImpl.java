package ru.amalyugin.orderservice.workflow.impl;

import java.time.Duration;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;

import lombok.extern.slf4j.Slf4j;
import ru.amalyugin.orderservice.activity.*;
import ru.amalyugin.orderservice.dto.AuditRecordDto;
import ru.amalyugin.orderservice.dto.OrderToCreateDto;
import ru.amalyugin.orderservice.workflow.OrderCreateWorkflow;

@Slf4j
@WorkflowImpl(taskQueues = "order-create-queue")
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

    private final UtilActivity utilActivity = Workflow.newActivityStub(
            UtilActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(1))
                    .build()
    );

    private final ExternalSystemActivities externalSystemActivities = Workflow.newActivityStub(
            ExternalSystemActivities.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(7))
                    .setRetryOptions(
                            RetryOptions.newBuilder()
                                    .setInitialInterval(Duration.ofSeconds(1))
                                    .setMaximumInterval(Duration.ofSeconds(10))
                                    .setMaximumAttempts(5)
                                    .build())
                    .build()
    );

    private final NotificationActivities notificationActivities = Workflow.newActivityStub(
            NotificationActivities.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .build()
    );

    @Override
    public String createOrder(OrderToCreateDto dto) {
        String uuid = utilActivity.generateUUID();

        // TRY SEND NOTIFICATION ASYNC
        Promise<Void> notificationPromise = Async.procedure(() ->
                notificationActivities.sendNotification(dto.username()));

        // TRY TO DOWNLOAD EXTERNAL SERVICE INFO
        Promise<String> vendorPromise = Async.function(
                externalSystemActivities::getExternalInfo,
                ExternalSystemActivities.ExternalSource.VENDOR,
                dto.vendorId()
        );
        Promise<String> orgPromise = Async.function(
                externalSystemActivities::getExternalInfo,
                ExternalSystemActivities.ExternalSource.ORG,
                dto.orgId()
        );

        String vendorName = vendorPromise.get();
        String orgName = orgPromise.get();

        log.info("External Services answer: {}, {}", vendorName, orgName);

        try {
            orderDaoActivity.saveOrder(dto);
        } catch (Exception exception) {
            auditActivity.saveAuditRecord(new AuditRecordDto(
                    uuid,
                    dto.requestId(),
                    AuditRecordDto.AuditStatus.ERROR
            ));
        }

        notificationPromise.get();

        auditActivity.saveAuditRecord(new AuditRecordDto(
                uuid,
                dto.requestId(),
                AuditRecordDto.AuditStatus.SUCCESS
        ));

        return dto.description();
    }

}
