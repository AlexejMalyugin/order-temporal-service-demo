package ru.amalyugin.orderservice.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.testing.TestWorkflowExtension;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.amalyugin.orderservice.activity.*;
import ru.amalyugin.orderservice.dto.OrderToCreateDto;
import ru.amalyugin.orderservice.workflow.impl.OrderCreateWorkflowImpl;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class OrderCreateWorkflowImplTest {

    @Mock(withoutAnnotations = true)
    private AuditActivity auditActivity;

    @Mock(withoutAnnotations = true)
    private OrderDaoActivity orderDaoActivity;

    @Mock(withoutAnnotations = true)
    private UtilActivity utilActivity;

    @Mock(withoutAnnotations = true)
    private ExternalSystemActivities externalSystemActivities;

    @Mock(withoutAnnotations = true)
    private NotificationActivities notificationActivities;

    @RegisterExtension
    public static final TestWorkflowExtension workflowExtension = TestWorkflowExtension.newBuilder()
            .setWorkflowTypes(OrderCreateWorkflowImpl.class)
            .setDoNotStart(true)
            .build();

    @Test
    void createOrderTest(
            TestWorkflowEnvironment environment,
            Worker worker,
            OrderCreateWorkflow workflow
    ) {
        OrderToCreateDto dto = new OrderToCreateDto(
                "USERNAME",
                System.currentTimeMillis(),
                "DESC",
                143535L,
                121L
        );
        when(orderDaoActivity.saveOrder(any())).thenReturn(new OrderDaoActivity.SaveOrderInputDto(1));
        when(utilActivity.generateUUID()).thenReturn(UUID.randomUUID().toString());
        when(externalSystemActivities.getExternalInfo(any()))
                .thenReturn(new ExternalSystemActivities.ExternalInfoOutputDto(UUID.randomUUID().toString()));

        worker.registerActivitiesImplementations(
                orderDaoActivity,
                auditActivity,
                utilActivity,
                externalSystemActivities,
                notificationActivities
        );

        environment.start();

        String output = workflow.createOrder(dto);

        assertEquals("DESC", output);
    }

}
