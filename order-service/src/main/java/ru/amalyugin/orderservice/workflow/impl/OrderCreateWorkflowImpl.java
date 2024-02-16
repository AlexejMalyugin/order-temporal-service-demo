package ru.amalyugin.orderservice.workflow.impl;

import java.time.Duration;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;

import org.slf4j.Logger;
import ru.amalyugin.orderservice.activity.*;
import ru.amalyugin.orderservice.activity.ExternalSystemActivities.ExternalInfoDto;
import ru.amalyugin.orderservice.activity.ExternalSystemActivities.ExternalInfoOutputDto;
import ru.amalyugin.orderservice.activity.NotificationActivities.NotificationRequestDto;
import ru.amalyugin.orderservice.dto.AuditRecordDto;
import ru.amalyugin.orderservice.dto.OrderToCreateDto;
import ru.amalyugin.orderservice.workflow.OrderCreateWorkflow;

import static ru.amalyugin.orderservice.activity.ExternalSystemActivities.ExternalSource.ORG;
import static ru.amalyugin.orderservice.activity.ExternalSystemActivities.ExternalSource.VENDOR;
import static ru.amalyugin.orderservice.constants.TemporalConst.CREATE_ORDER_QUEUE_NAME;

@WorkflowImpl(taskQueues = CREATE_ORDER_QUEUE_NAME)
public class OrderCreateWorkflowImpl implements OrderCreateWorkflow {

    public static final Logger logger = Workflow.getLogger(OrderCreateWorkflowImpl.class);

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
                                    .setBackoffCoefficient(1.5)
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
        logger.debug("Workflow started");
        String uuid = utilActivity.generateUUID();

        logger.debug("Send notification process starter");
        // TRY SEND NOTIFICATION ASYNC
        Promise<Void> notificationPromise = Async.procedure(() ->
                notificationActivities.sendNotification(new NotificationRequestDto(dto.username())));

        logger.debug("Get external data processes started");
        // TRY TO DOWNLOAD EXTERNAL SERVICE INFO
        Promise<ExternalInfoOutputDto> vendorPromise = Async.function(
                externalSystemActivities::getExternalInfo,
                new ExternalInfoDto(VENDOR, dto.vendorId())
        );
        Promise<ExternalInfoOutputDto> orgPromise = Async.function(
                externalSystemActivities::getExternalInfo,
                new ExternalInfoDto(ORG, dto.orgId())
        );

        String vendorName = vendorPromise.get().input();
        String orgName = orgPromise.get().input();

        logger.debug("External Services answer: {}, {}", vendorName, orgName);

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

        logger.debug("Workflow process finished");

        return dto.description();
    }

}
